import os
import re
import json
import base64
from datetime import datetime
from email import message_from_bytes
from email.utils import parsedate_to_datetime
from bs4 import BeautifulSoup
from transformers import T5Tokenizer, T5ForConditionalGeneration
import torch
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from google.auth.transport.requests import Request

# --- Google API Configuration ---
SCOPES = ['https://www.googleapis.com/auth/gmail.modify']

# --- Load FLAN-T5 Locally ---
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
tokenizer = T5Tokenizer.from_pretrained("google/flan-t5-large")
flan_model = T5ForConditionalGeneration.from_pretrained("google/flan-t5-large").to(device)

def run_flan(prompt, max_length=64):
    inputs = tokenizer(prompt, return_tensors="pt", truncation=True).to(device)
    outputs = flan_model.generate(**inputs, max_length=max_length)
    return tokenizer.decode(outputs[0], skip_special_tokens=True).strip()

def ai_guess_role(text, subject=""):
    prompt = f"""Extract the job title or role the user applied for from this job-related email.

Email:
\"\"\"{subject}\n{text}\"\"\"

Only respond with the role name."""
    result = run_flan(prompt)
    return result if result and "unknown" not in result.lower() else None

def ai_guess_status(text, subject=""):
    prompt = f"""Classify the current application status based on this job-related email. Choose from:
Applied, Rejected, Interview Scheduled, Shortlisted, Pending, or Unknown.

Email:
\"\"\"{subject}\n{text}\"\"\"

Only respond with one word from the list."""
    result = run_flan(prompt)
    return result if result and "unknown" not in result.lower() else None

def ai_guess_company(subject, body):
    prompt = f"""Extract the name of the company from the following job-related email.

Email:
\"\"\"{subject}\n{body}\"\"\"

Only respond with the company name:"""
    result = run_flan(prompt)
    return result if result and "unknown" not in result.lower() else None

def connect_to_gmail():
    creds = None
    if os.path.exists('token.json'):
        creds = Credentials.from_authorized_user_file('token.json', SCOPES)
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file('credentials.json', SCOPES)
            creds = flow.run_local_server(port=0)
        with open('token.json', 'w') as token:
            token.write(creds.to_json())
    return build('gmail', 'v1', credentials=creds)

def extract_text_from_html(html_content):
    soup = BeautifulSoup(html_content, "html.parser")
    return soup.get_text(separator="\n", strip=True)

def fetch_emails(service):
    results = service.users().messages().list(userId='me', labelIds=['INBOX', 'UNREAD'], q='is:unread').execute()
    messages = results.get('messages', [])
    emails = []

    if not messages:
        print("\u2705 No unread emails found.")
        return emails

    for msg in messages:
        msg_data = service.users().messages().get(userId='me', id=msg['id'], format='raw').execute()
        raw_msg = base64.urlsafe_b64decode(msg_data['raw'].encode('ASCII'))
        mime_msg = message_from_bytes(raw_msg)

        subject = mime_msg['Subject'] or ""
        sender = mime_msg['From'] or ""
        try:
            parsed_date = parsedate_to_datetime(mime_msg['Date'])
            date_received = parsed_date.strftime('%Y-%m-%d')
        except:
            date_received = datetime.today().strftime('%Y-%m-%d')

        match = re.search(r"@([\w\-]+)", sender)
        sender_domain = match.group(1).capitalize() if match else "Unknown"

        body = ""
        if mime_msg.is_multipart():
            for part in mime_msg.walk():
                content_type = part.get_content_type()
                content_dispo = str(part.get("Content-Disposition"))

                if content_type == "text/plain" and "attachment" not in content_dispo:
                    charset = part.get_content_charset() or "utf-8"
                    try:
                        body += part.get_payload(decode=True).decode(charset, errors="ignore")
                    except:
                        continue
                elif content_type == "text/html" and not body:
                    charset = part.get_content_charset() or "utf-8"
                    try:
                        body += part.get_payload(decode=True).decode(charset, errors="ignore")
                    except:
                        continue
        else:
            charset = mime_msg.get_content_charset() or "utf-8"
            try:
                body = mime_msg.get_payload(decode=True).decode(charset, errors="ignore")
            except:
                body = ""

        cleaned_body = extract_text_from_html(body)

        if is_job_related(subject, cleaned_body):
            emails.append({
                "subject": subject,
                "body": cleaned_body,
                "sender_email": sender,
                "sender_domain": sender_domain,
                "date_received": date_received
            })

        service.users().messages().modify(userId='me', id=msg['id'], body={"removeLabelIds": ["UNREAD"]}).execute()

    return emails

def is_job_related(subject, body):
    job_keywords = [
        "job", "application", "interview", "offer", "position", "role",
        "hiring", "career", "resume", "candidate", "recruitment"
    ]
    subject = subject.lower()
    body = body.lower()
    return any(keyword in subject or keyword in body for keyword in job_keywords)

def regex_guess_status(text):
    text = text.lower()
    status_patterns = {
        "Rejected": [r"\bnot selected\b", r"\bunsuccessful\b", r"\brejected\b"],
        "Interview Scheduled": [r"\binterview\b", r"\bscheduled\b"],
        "Shortlisted": [r"\bshortlisted\b", r"\bselected for next round\b"],
        "Pending": [r"\bunder review\b", r"\bpending\b"],
        "Applied": [r"\bthank you for applying\b", r"\bapplication received\b"]
    }
    for status, patterns in status_patterns.items():
        for pattern in patterns:
            if re.search(pattern, text):
                return status
    return "Unknown"

def regex_guess_role(text):
    match = re.search(r"(?:position|role|title).*?:?\s*([A-Za-z\s]+)", text, re.IGNORECASE)
    if match:
        return match.group(1).strip()
    return "Unknown"

def extract_company_name(subject, body, sender_email):
    company = ai_guess_company(subject, body)
    if company and "unknown" not in company.lower():
        return company.strip()

    patterns = [
        r"applied to\s+(.*?)\s+at\s+([A-Z][a-zA-Z0-9&\s\-]+)",
        r"application (to|for).*?at\s+([A-Z][a-zA-Z0-9&\s\-]+)",
        r"thank you for applying to\s+([A-Z][a-zA-Z0-9&\s\-]+)",
        r"we received your application to\s+([A-Z][a-zA-Z0-9&\s\-]+)",
        r"you applied to\s+([A-Z][a-zA-Z0-9&\s\-]+)"
    ]
    combined_text = subject + "\n" + body
    for pattern in patterns:
        match = re.search(pattern, combined_text, re.IGNORECASE)
        if match:
            value = match.group(2 if len(match.groups()) > 1 else 1).strip()
            if any(keyword in value.lower() for keyword in ["engineer", "developer", "intern", "position", "role"]):
                continue
            return value

    domain_match = re.search(r"@([\w\-]+)", sender_email)
    if domain_match:
        return domain_match.group(1).capitalize()

    return "Unknown"

def calculate_days_since(date_str):
    applied_date = datetime.strptime(date_str, "%Y-%m-%d")
    return (datetime.today() - applied_date).days

def extract_info(email_data):
    subject = email_data.get("subject", "")
    text = email_data['body']
    sender_email = email_data.get("sender_email", "")
    sender_domain = email_data['sender_domain']
    received_date = email_data['date_received']

    company_name = extract_company_name(subject, text, sender_email)
    role = ai_guess_role(text, subject) or regex_guess_role(text)
    status = ai_guess_status(text, subject) or regex_guess_status(text)

    return {
        "company_name": company_name,
        "date_applied": received_date,
        "days_since_update": calculate_days_since(received_date),
        "role_applied_for": role,
        "status": status
    }

def main():
    service = connect_to_gmail()
    emails = fetch_emails(service)
    extracted_data = []

    for email_data in emails:
        print(f"\n\U0001F4E8 Subject: {email_data['subject']}")
        extracted = extract_info(email_data)
        print(f"\u2705 Extracted: {extracted}")
        extracted_data.append(extracted)

    with open("email_applications.json", "w") as f:
        json.dump(extracted_data, f, indent=2)

    print("\n\U0001F4C1 Saved data to email_applications.json")

if __name__ == "__main__":
    main()

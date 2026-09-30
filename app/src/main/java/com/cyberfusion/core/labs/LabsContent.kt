package com.cyberfusion.core.labs

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object LabsContent {
    private val json = Json { ignoreUnknownKeys = true }

    val allLabs: List<LabContent> = listOf(
        phishingInvestigationLab(),
        iocAnalysisLab(),
        malwareInvestigationLab(),
        incidentResponseLab(),
        threatIntelligenceLab(),
        logAnalysisLab(),
        grcRiskAssessmentLab(),
        socInvestigationLab(),
        cloudCredentialCompromiseLab(),
        ransomwareResponseLab(),
        threatHuntingLab(),
        webExploitationLab(),
        malwareSandboxLab(),
        digitalForensicsLab(),
        activeDefenseLab()
    )

    fun phishingInvestigationLab(): LabContent = LabContent(
        id = 1,
        title = "Lab 01 — Phishing Investigation",
        description = "Analyze a phishing email campaign and identify indicators of compromise.",
        category = "Phishing",
        difficulty = "Beginner",
        scenario = """
            You are a security analyst at Acme Corp. The SOC has received multiple reports from employees about a suspicious email campaign.
            
            Email Subject: "Urgent: Your Account Will Be Suspended"
            From: security@acmecorp-support.com
            Body: "Dear Employee, Your account will be suspended in 24 hours. Click here to verify: http://acmecorp-login.verify-security.com/login"
            
            Headers show:
            - Return-Path: noreply@mail-service.xyz
            - X-Mailer: PHPMailer 5.2
            - SPF: softfail
            - DKIM: none
        """.trimIndent(),
        evidence = "URL: http://acmecorp-login.verify-security.com/login\nIP: 192.168.45.123\nSender: security@acmecorp-support.com\nSubject: Urgent: Your Account Will Be Suspended",
        hints = mapOf(
            1 to "Check the sender domain carefully. Does it match the legitimate company domain?",
            2 to "Look at the URL structure. Is the domain legitimate or suspicious?",
            3 to "SPF softfail indicates the sending server is not authorized to send on behalf of the domain.",
            4 to "Consider what type of attack this is and what actions you should recommend."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What is the most suspicious element in the sender address?",
                options = listOf(
                    "The email is from security@acmecorp-support.com",
                    "The email uses HTTPS",
                    "The email has a professional greeting",
                    "The email mentions account suspension"
                ),
                correctAnswer = 0,
                explanation = "The domain 'acmecorp-support.com' is not the legitimate 'acmecorp.com' domain. Attackers use lookalike domains to trick victims."
            ),
            LabQuestion(
                id = 2,
                question = "What type of attack is this?",
                options = listOf(
                    "Malware infection",
                    "Phishing attack",
                    "DDoS attack",
                    "Man-in-the-middle attack"
                ),
                correctAnswer = 1,
                explanation = "This is a phishing attack. The attacker impersonates a legitimate service to steal credentials."
            ),
            LabQuestion(
                id = 3,
                question = "What does SPF softfail indicate?",
                options = listOf(
                    "The email is from a trusted source",
                    "The sending server is not authorized to send on behalf of the domain",
                    "The email contains malware",
                    "The email is encrypted"
                ),
                correctAnswer = 1,
                explanation = "SPF softfail means the sending server is not authorized to send on behalf of the claimed domain, which is a strong indicator of spoofing."
            ),
            LabQuestion(
                id = 4,
                question = "What is the recommended action?",
                options = listOf(
                    "Click the link to verify",
                    "Reply to the email for more information",
                    "Report to SOC and block the domain",
                    "Forward to colleagues"
                ),
                correctAnswer = 2,
                explanation = "The correct action is to report to the SOC, block the malicious domain, and warn employees. Never click suspicious links."
            )
        ),
        debrief = "This phishing attempt used lookalike domains and urgency to trick employees. Always verify sender domains and hover over links before clicking."
    )

    fun iocAnalysisLab(): LabContent = LabContent(
        id = 2,
        title = "Lab 02 — IOC Analysis",
        description = "Analyze indicators of compromise from a security incident.",
        category = "IOC Analysis",
        difficulty = "Beginner",
        scenario = """
            During an incident response, you discover the following IOCs:
            
            1. IP Address: 203.0.113.45
            2. Domain: malware-c2.net
            3. File Hash: 44d88612fea8a8f36de82e1278abb02f
            4. Email: admin@company-update.org
            
            You need to classify and prioritize these IOCs.
        """.trimIndent(),
        evidence = "IP: 203.0.113.45\nDomain: malware-c2.net\nHash: 44d88612fea8a8f36de82e1278abb02f\nEmail: admin@company-update.org",
        hints = mapOf(
            1 to "Consider which IOC type is most useful for network monitoring.",
            2 to "File hashes are useful for what type of detection?",
            3 to "Which IOC would you use to block at the firewall?",
            4 to "Think about the priority based on impact and ease of detection."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "Which IOC is best for network monitoring?",
                options = listOf(
                    "File hash",
                    "IP address",
                    "Email address",
                    "File name"
                ),
                correctAnswer = 1,
                explanation = "IP addresses are ideal for network monitoring and firewall blocking. You can filter traffic to/from suspicious IPs."
            ),
            LabQuestion(
                id = 2,
                question = "What is the file hash most useful for?",
                options = listOf(
                    "Network filtering",
                    "Endpoint detection and antivirus",
                    "Email filtering",
                    "User training"
                ),
                correctAnswer = 1,
                explanation = "File hashes are used for endpoint detection, antivirus scanning, and file integrity monitoring."
            ),
            LabQuestion(
                id = 3,
                question = "Which IOC would you block at the firewall first?",
                options = listOf(
                    "File hash",
                    "IP address",
                    "Email address",
                    "File size"
                ),
                correctAnswer = 1,
                explanation = "IP addresses can be blocked at the firewall to prevent C2 communication. This is the highest priority action."
            ),
            LabQuestion(
                id = 4,
                question = "What is the correct priority order?",
                options = listOf(
                    "Email → IP → Hash → Domain",
                    "IP → Domain → Hash → Email",
                    "Hash → IP → Email → Domain",
                    "Domain → Hash → IP → Email"
                ),
                correctAnswer = 1,
                explanation = "IP blocking stops immediate C2. Domain blocking prevents resolution. Hash detection catches malware. Email rules prevent initial access."
            )
        ),
        debrief = "IOC analysis requires understanding how each indicator type is used in detection and response. Prioritize based on impact and ease of implementation."
    )

    fun malwareInvestigationLab(): LabContent = LabContent(
        id = 3,
        title = "Lab 03 — Malware Investigation",
        description = "Investigate a malware sample and determine its capabilities and impact.",
        category = "Malware",
        difficulty = "Intermediate",
        scenario = """
            A user reported their laptop is behaving strangely. The SOC isolated the machine and extracted a suspicious file:
            
            File: invoice_2024.pdf.exe
            Size: 245,760 bytes
            MD5: 44d88612fea8a8f36de82e1278abb02f
            SHA256: 7b8f9e2d1c3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9
            
            Initial analysis shows:
            - Packed with UPX
            - Connects to 203.0.113.45 on port 443
            - Creates a service named "SystemUpdateService"
            - Encrypts files with .locked extension
        """.trimIndent(),
        evidence = "File: invoice_2024.pdf.exe\nMD5: 44d88612fea8a8f36de82e1278abb02f\nSHA256: 7b8f9e2d1c3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3c4d5e6f7a8b9\nC2: 203.0.113.45:443\nService: SystemUpdateService\nExtension: .locked",
        hints = mapOf(
            1 to "The file extension .pdf.exe is a common social engineering tactic.",
            2 to "UPX packing is often used to obfuscate malware.",
            3 to "The .locked extension suggests ransomware behavior.",
            4 to "Creating a service is a persistence mechanism."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What social engineering technique is used in the filename?",
                options = listOf(
                    "Double extension to hide the real file type",
                    "Using a PDF icon",
                    "Sending from a trusted sender",
                    "Using urgency in the subject"
                ),
                correctAnswer = 0,
                explanation = "The double extension .pdf.exe tricks users into thinking the file is a PDF when it's actually an executable."
            ),
            LabQuestion(
                id = 2,
                question = "What type of malware is this likely to be?",
                options = listOf(
                    "Trojan",
                    "Ransomware",
                    "Spyware",
                    "Adware"
                ),
                correctAnswer = 1,
                explanation = "The .locked extension and encryption behavior strongly indicate ransomware."
            ),
            LabQuestion(
                id = 3,
                question = "What is the purpose of creating 'SystemUpdateService'?",
                options = listOf(
                    "To update the system",
                    "Persistence - to survive reboots",
                    "To improve performance",
                    "To download updates"
                ),
                correctAnswer = 1,
                explanation = "Creating a service is a persistence mechanism. The malware uses it to survive system reboots and maintain execution."
            ),
            LabQuestion(
                id = 4,
                question = "What is the recommended immediate action?",
                options = listOf(
                    "Pay the ransom",
                    "Restore from backup and rebuild the system",
                    "Try to decrypt files manually",
                    "Negotiate with attackers"
                ),
                correctAnswer = 1,
                explanation = "The safest approach is to restore from known-good backups and rebuild. Never pay ransomware demands."
            )
        ),
        debrief = "Malware investigation requires analyzing static properties, behavior, and impact. Ransomware should be handled by isolating systems and restoring from backups."
    )

    fun incidentResponseLab(): LabContent = LabContent(
        id = 4,
        title = "Lab 04 — Incident Response",
        description = "Respond to a ransomware incident following proper IR procedures.",
        category = "Incident Response",
        difficulty = "Intermediate",
        scenario = """
            At 02:00 AM, the SOC receives an alert: multiple servers are displaying ransomware notes.
            
            Affected systems:
            - File Server (FS01): 500 GB encrypted
            - Database Server (DB01): Customer data encrypted
            - Backup Server (BK01): Backups encrypted
            
            Ransom note demands $500,000 in Bitcoin.
            
            Timeline:
            - 01:15 AM: Unusual outbound traffic from FS01
            - 01:30 AM: Alert triggered for unusual file modifications
            - 01:45 AM: Ransom notes discovered on multiple systems
        """.trimIndent(),
        evidence = "Affected: FS01, DB01, BK01\nRansom demand: $500,000 BTC\nEncrypted: 500 GB + customer data + backups\nTimeline: 01:15 - 01:45 AM",
        hints = mapOf(
            1 to "What is the first step in incident response?",
            2 to "Consider the order of systems affected. What does this tell you?",
            3 to "Why is the backup server being encrypted a critical issue?",
            4 to "What are the recovery options when backups are compromised?"
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What is the FIRST action you should take?",
                options = listOf(
                    "Pay the ransom",
                    "Isolate affected systems from the network",
                    "Try to decrypt files",
                    "Call the attackers"
                ),
                correctAnswer = 1,
                explanation = "The first step is to isolate affected systems to prevent spread. This includes disconnecting from the network and disabling remote access."
            ),
            LabQuestion(
                id = 2,
                question = "What does the timeline suggest about the attack?",
                options = listOf(
                    "It was a targeted attack with reconnaissance",
                    "It was a random opportunistic attack",
                    "It was an insider threat",
                    "It was a false positive"
                ),
                correctAnswer = 0,
                explanation = "The progression from unusual traffic to encryption suggests a deliberate, multi-stage attack with some level of reconnaissance."
            ),
            LabQuestion(
                id = 3,
                question = "Why is the backup server encryption critical?",
                options = listOf(
                    "It increases the ransom demand",
                    "It eliminates the primary recovery option",
                    "It affects only one system",
                    "It doesn't matter"
                ),
                correctAnswer = 1,
                explanation = "When backups are encrypted, the primary recovery option (restoring from backup) is compromised. This significantly increases the impact."
            ),
            LabQuestion(
                id = 4,
                question = "What is the recommended recovery strategy?",
                options = listOf(
                    "Pay the ransom immediately",
                    "Restore from any available offsite backups",
                    "Rebuild systems from scratch",
                    "Attempt to negotiate a lower ransom"
                ),
                correctAnswer = 1,
                explanation = "If any offsite or air-gapped backups exist, they should be used for recovery. Rebuilding from scratch may be necessary if all backups are compromised."
            )
        ),
        debrief = "Incident response requires systematic containment, eradication, and recovery. Always maintain offsite backups and test recovery procedures regularly."
    )

    fun threatIntelligenceLab(): LabContent = LabContent(
        id = 5,
        title = "Lab 05 — Threat Intelligence",
        description = "Gather and analyze threat intelligence on a suspicious IP address.",
        category = "Threat Intelligence",
        difficulty = "Beginner",
        scenario = """
            Your SOC identified unusual outbound traffic to IP 198.51.100.42.
            
            You need to investigate this IP using threat intelligence sources.
            
            Available tools:
            - AbuseIPDB
            - ThreatFox
            - MalwareBazaar
            - VirusTotal
        """.trimIndent(),
        evidence = "IP: 198.51.100.42\nTraffic: Outbound on port 443\nFrequency: 500+ connections in 1 hour\nDestination: Unknown",
        hints = mapOf(
            1 to "Start with AbuseIPDB to check IP reputation.",
            2 to "ThreatFox can provide IOC context and malware associations.",
            3 to "Consider the confidence score and reported abuse.",
            4 to "Document findings and recommend actions based on risk."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What does a high abuse confidence score indicate?",
                options = listOf(
                    "The IP is safe",
                    "The IP has been reported for malicious activity",
                    "The IP is a known CDN",
                    "The IP is internal"
                ),
                correctAnswer = 1,
                explanation = "A high abuse confidence score means the IP has been reported by multiple sources for malicious activities."
            ),
            LabQuestion(
                id = 2,
                question = "What should you do if ThreatFox associates the IP with known malware?",
                options = listOf(
                    "Ignore it",
                    "Block the IP and investigate affected systems",
                    "Send an email to the IP owner",
                    "Wait for more information"
                ),
                correctAnswer = 1,
                explanation = "If the IP is associated with known malware, immediate blocking and investigation is required."
            ),
            LabQuestion(
                id = 3,
                question = "What is the value of correlating multiple TI sources?",
                options = listOf(
                    "It slows down the investigation",
                    "It increases confidence in the assessment",
                    "It costs more money",
                    "It is not useful"
                ),
                correctAnswer = 1,
                explanation = "Correlating multiple threat intelligence sources increases confidence and reduces false positives."
            ),
            LabQuestion(
                id = 4,
                question = "What is the final recommendation for a high-risk IP?",
                options = listOf(
                    "Allow the traffic",
                    "Block at firewall and investigate endpoints",
                    "Monitor only",
                    "Restart the firewall"
                ),
                correctAnswer = 1,
                explanation = "For high-risk IPs, block at the firewall and investigate any endpoints that communicated with it."
            )
        ),
        debrief = "Threat intelligence gathering combines multiple sources to build a complete picture. Always validate findings and consider the confidence level of each source."
    )

    fun logAnalysisLab(): LabContent = LabContent(
        id = 6,
        title = "Lab 06 — Log Analysis",
        description = "Analyze Windows Event Logs to identify suspicious activity.",
        category = "Log Analysis",
        difficulty = "Intermediate",
        scenario = """
            You are investigating a compromised Windows server. You have the following logs:
            
            Event ID 4624: Multiple logons from 192.168.1.100
            Event ID 4698: Scheduled task created: "SystemUpdate"
            Event ID 4688: Process created: powershell.exe -enc <base64>
            Event ID 5140: Network share access: \\\\fileserver\\share
            Event ID 1102: Audit log cleared
            
            Time range: 2024-01-15 02:00 - 04:00
        """.trimIndent(),
        evidence = "4624: Multiple logons from 192.168.1.100\n4698: Scheduled task 'SystemUpdate'\n4688: PowerShell encoded command\n5140: Network share access\n1102: Audit log cleared",
        hints = mapOf(
            1 to "Event ID 4624 with many logons could indicate brute force.",
            2 to "PowerShell encoded commands are often used to obfuscate malicious activity.",
            3 to "Clearing audit logs is a common anti-forensics technique.",
            4 to "Consider the order of events to reconstruct the attack."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What does Event ID 4688 with PowerShell -enc suggest?",
                options = listOf(
                    "Normal system administration",
                    "Obfuscated malicious activity",
                    "Windows update",
                    "User error"
                ),
                correctAnswer = 1,
                explanation = "PowerShell with encoded commands (-enc) is a common technique to obfuscate malicious scripts and bypass detection."
            ),
            LabQuestion(
                id = 2,
                question = "What is the significance of Event ID 1102?",
                options = listOf(
                    "It indicates a successful login",
                    "It shows the audit log was cleared",
                    "It indicates a network connection",
                    "It shows a process creation"
                ),
                correctAnswer = 1,
                explanation = "Event ID 1102 indicates the security audit log was cleared. This is a common anti-forensics technique used by attackers."
            ),
            LabQuestion(
                id = 3,
                question = "What attack pattern is suggested by these logs?",
                options = listOf(
                    "Phishing",
                    "Brute force → privilege escalation → persistence → anti-forensics",
                    "DDoS",
                    " insider trading"
                ),
                correctAnswer = 1,
                explanation = "The logs show: brute force logons (4624), persistence (4698), malicious execution (4688), and anti-forensics (1102)."
            ),
            LabQuestion(
                id = 4,
                question = "What is the correct response sequence?",
                options = listOf(
                    "Ignore and monitor",
                    "Isolate → preserve logs → investigate → remediate",
                    "Reboot the server",
                    "Wait for more logs"
                ),
                correctAnswer = 1,
                explanation = "The correct IR sequence is to isolate the system, preserve existing logs, investigate the scope, and then remediate."
            )
        ),
        debrief = "Log analysis requires correlating events across time and sources. Look for patterns of brute force, execution, persistence, and anti-forensics."
    )

    fun grcRiskAssessmentLab(): LabContent = LabContent(
        id = 7,
        title = "Lab 07 — GRC Risk Assessment",
        description = "Perform a risk assessment for a cloud migration project.",
        category = "GRC",
        difficulty = "Advanced",
        scenario = """
            Your organization plans to migrate customer data to a cloud provider.
            
            Current state:
            - 100 TB of customer data
            - 500 employees with access
            - Regulated industry (healthcare)
            
            Proposed cloud provider:
            - AWS us-east-1
            - SOC 2 Type II certified
            - No explicit HIPAA BAA offered
            
            Risks identified:
            - Data residency requirements
            - Access control complexity
            - Incident response in cloud
            - Vendor lock-in
        """.trimIndent(),
        evidence = "Data: 100 TB customer\nUsers: 500 employees\nRegulation: Healthcare\nProvider: AWS us-east-1\nCert: SOC 2 Type II\nBAA: Not offered",
        hints = mapOf(
            1 to "Consider the regulatory requirements for healthcare data.",
            2 to "Evaluate the likelihood and impact of each risk.",
            3 to "Think about controls that could mitigate the risks.",
            4 to "Consider both technical and procedural controls."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What is the primary regulatory concern?",
                options = listOf(
                    "GDPR",
                    "HIPAA",
                    "PCI DSS",
                    "SOX"
                ),
                correctAnswer = 1,
                explanation = "Healthcare data is regulated by HIPAA in the US. A Business Associate Agreement (BAA) is required for cloud providers handling PHI."
            ),
            LabQuestion(
                id = 2,
                question = "How should you classify the risk of 'No explicit HIPAA BAA'?",
                options = listOf(
                    "Low likelihood, low impact",
                    "High likelihood, high impact",
                    "Low likelihood, high impact",
                    "High likelihood, low impact"
                ),
                correctAnswer = 1,
                explanation = "Without a BAA, the organization cannot legally use the cloud provider for PHI. This is both likely to occur and has high regulatory impact."
            ),
            LabQuestion(
                id = 3,
                question = "Which control addresses data residency?",
                options = listOf(
                    "Firewall rules",
                    "Geo-fencing and data classification",
                    "Antivirus",
                    "Password policy"
                ),
                correctAnswer = 1,
                explanation = "Geo-fencing ensures data stays in required jurisdictions. Data classification helps apply appropriate controls."
            ),
            LabQuestion(
                id = 4,
                question = "What is the recommended next step?",
                options = listOf(
                    "Migrate immediately",
                    "Negotiate BAA and implement compensating controls",
                    "Abandon cloud migration",
                    "Use any cloud provider"
                ),
                correctAnswer = 1,
                explanation = "Negotiate a BAA with the provider and implement compensating controls (encryption, access controls, monitoring) while the BAA is in progress."
            )
        ),
        debrief = "GRC risk assessment requires understanding regulatory requirements, evaluating likelihood and impact, and implementing appropriate controls. Document everything for audit trails."
    )

    fun socInvestigationLab(): LabContent = LabContent(
        id = 8,
        title = "Lab 08 — SOC Investigation",
        description = "Conduct a full SOC investigation from alert to resolution.",
        category = "SOC",
        difficulty = "Advanced",
        scenario = """
            SOC Alert #4521:
            - Rule: Multiple Failed Logons Followed by Success
            - User: jsmith
            - Source IP: 203.0.113.45
            - Time: 2024-01-15 14:30 UTC
            
            Timeline:
            14:25 - 10 failed logons from 203.0.113.45
            14:30 - Successful logon from same IP
            14:32 - Process created: cmd.exe /c whoami /priv
            14:35 - New user account created: temp_admin
            
            You must investigate, contain, and document this incident.
        """.trimIndent(),
        evidence = "Alert: #4521\nUser: jsmith\nIP: 203.0.113.45\nFailed logons: 10\nSuccessful logon: 14:30\nProcess: cmd.exe /c whoami /priv\nNew account: temp_admin",
        hints = mapOf(
            1 to "This pattern indicates a brute force attack followed by privilege escalation.",
            2 to "Check if the user account was locked after failed attempts.",
            3 to "The new user account 'temp_admin' is a persistence mechanism.",
            4 to "Document all findings for the incident report."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What type of attack is indicated?",
                options = listOf(
                    "Phishing",
                    "Brute force + privilege escalation",
                    "DDoS",
                    "Insider threat"
                ),
                correctAnswer = 1,
                explanation = "10 failed logons followed by a successful logon indicates a brute force attack. The subsequent commands indicate privilege escalation."
            ),
            LabQuestion(
                id = 2,
                question = "What is the immediate containment action?",
                options = listOf(
                    "Send an email to the user",
                    "Disable the compromised account and lock the source IP",
                    "Wait for more alerts",
                    "Reset the user's password only"
                ),
                correctAnswer = 1,
                explanation = "Immediate containment requires disabling the compromised account and blocking the attacker's IP at the firewall."
            ),
            LabQuestion(
                id = 3,
                question = "What does the 'temp_admin' account suggest?",
                options = listOf(
                    "Normal IT activity",
                    "Persistence mechanism",
                    "Service account",
                    "User mistake"
                ),
                correctAnswer = 1,
                explanation = "Creating a new admin account during a breach is a classic persistence mechanism to maintain access even if the initial compromise is discovered."
            ),
            LabQuestion(
                id = 4,
                question = "What should be included in the incident report?",
                options = listOf(
                    "Only the timeline",
                    "Timeline, IOCs, impact, root cause, and remediation",
                    "Just the IP address",
                    "Nothing, it's not required"
                ),
                correctAnswer = 1,
                explanation = "A complete incident report includes timeline, IOCs, impact assessment, root cause analysis, and remediation steps."
            )
        ),
        debrief = "SOC investigations require methodical analysis from alert to resolution. Document everything, contain quickly, and always look for persistence mechanisms."
    )

    // ── Advanced track: deeper, multi-stage scenarios ──────────────────────────

    fun cloudCredentialCompromiseLab(): LabContent = LabContent(
        id = 9,
        title = "Lab 09 — Cloud Identity Compromise",
        description = "Trace an OAuth consent-phishing attack into Microsoft 365 and contain a persistent cloud foothold.",
        category = "Cloud Security",
        difficulty = "Intermediate",
        scenario = """
            Your identity protection stack flagged a series of anomalous sign-ins for a finance user at Acme Corp.

            Timeline (UTC):
            09:14 - User 'cfo@acmecorp.com' consents to a third-party OAuth app "Invoice++ PDF Converter"
                    requesting Mail.ReadWrite, Files.ReadWrite.All and Contacts.Read
            09:16 - Sign-in from AS14061 DigitalOcean, IP 165.227.88.15, user-agent "python-requests/2.31"
            09:40 - Mail forwarding rule created: forward all inbox mail to archive-helper@invoice-apps.dev
            10:02 - 14 files downloaded from the Finance SharePoint document library
            10:47 - Impossible travel: second sign-in from 45.77.201.9 (Singapore) 33 minutes after the US sign-in

            Defender for Cloud Apps confirms the OAuth app was created 2 days ago by a tenant with no prior reputation.
        """.trimIndent(),
        evidence = "OAuth app: Invoice++ PDF Converter (unverified publisher)\nConsented scopes: Mail.ReadWrite, Files.ReadWrite.All\nForward rule target: archive-helper@invoice-apps.dev\nSource IPs: 165.227.88.15, 45.77.201.9\nAffected user: cfo@acmecorp.com",
        hints = mapOf(
            1 to "Illicit consent grants are the cloud equivalent of a phishing attachment — the payload is an app, not an exe.",
            2 to "The forwarding rule is the exfiltration channel; the OAuth token is the persistence mechanism.",
            3 to "Revoking the user's sessions kills the stolen token even if the app remains consented.",
            4 to "Check whether Financial data was touched before deciding on breach notification."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What was the initial access technique?",
                options = listOf(
                    "Brute-force password spray",
                    "Illicit OAuth consent grant (consent phishing)",
                    "On-prem VPN credential theft",
                    "SIM swap on the CFO's phone"
                ),
                correctAnswer = 1,
                explanation = "The user consented to a malicious OAuth app; the attacker then used the app's token to access mail and files — T1528 Steal Application Access Token."
            ),
            LabQuestion(
                id = 2,
                question = "Which artifact proves data EXFILTRATION (not just access)?",
                options = listOf(
                    "The python-requests user-agent",
                    "The inbox forwarding rule to invoice-apps.dev",
                    "The DigitalOcean IP geolocation",
                    "The unverified app publisher flag"
                ),
                correctAnswer = 1,
                explanation = "A hidden forwarding rule silently copies every incoming mail to the attacker — continuous exfiltration. The 14 downloaded files add bulk exfiltration on top."
            ),
            LabQuestion(
                id = 3,
                question = "What is the correct FIRST containment action?",
                options = listOf(
                    "Reset the CFO's password only",
                    "Revoke the OAuth app consent AND revoke the user's active sessions",
                    "Block the two source IPs at the firewall",
                    "Delete the forwarding rule and monitor"
                ),
                correctAnswer = 1,
                explanation = "Password reset does not kill existing refresh tokens. Revoke consent + revoke sessions (T1528 mitigation) to invalidate the stolen token immediately."
            ),
            LabQuestion(
                id = 4,
                question = "Which MITRE ATT&CK technique maps to the malicious consent?",
                options = listOf(
                    "T1566 Phishing",
                    "T1078 Valid Accounts",
                    "T1528 Steal Application Access Token",
                    "T1114 Email Collection"
                ),
                correctAnswer = 2,
                explanation = "T1528 covers abusing OAuth application tokens; T1114 (email collection) describes what the forwarding rule did afterwards."
            ),
            LabQuestion(
                id = 5,
                question = "Which control prevents recurrence org-wide?",
                options = listOf(
                    "Weekly password rotation policy",
                    "Block user consent to unverified apps; require admin approval for OAuth grants",
                    "Longer password minimum length",
                    "Disable legacy IMAP for the CFO only"
                ),
                correctAnswer = 1,
                explanation = "Tenant-level consent policy (block unverified publishers, admin consent workflow) removes the whole class of attack."
            )
        ),
        debrief = "Cloud incidents pivot on identity, not malware. Token revocation beats password resets, and consent governance is the durable fix."
    )

    fun ransomwareResponseLab(): LabContent = LabContent(
        id = 10,
        title = "Lab 10 — Ransomware Detonation & Recovery",
        description = "Contain an active LockBit-style deployment, map the kill chain and drive a defensible recovery.",
        category = "Incident Response",
        difficulty = "Advanced",
        scenario = """
            03:12 SOC pager: mass file-renames detected on FILESRV01, extension .lockbit appended.

            Forensic timeline reconstructed from EDR + backups catalog:
            D-3    - VPN appliance (unpatched CVE-2023-99999) exploited; attacker lands on DMZ jump host
            D-3+2h - Credentials dumped from LSASS (mimikatz signature), lateral move via SMB to FILESRV01
            D-2    - AdFind + net group enumeration; domain backup service account accessed
            D-1    - Backups catalog tampered: shadow copies deleted via vssadmin delete shadows /all
            D-0    - GPO-pushed payload encrypts shares at scale; ransom note dropped in every directory

            Current state: 2 of 30 servers still clean (isolated by EDR), offline tape backup from D-4 verified intact.
        """.trimIndent(),
        evidence = "Encryption extension: .lockbit\nInitial access: CVE-2023-99999 VPN appliance\nCredential access: LSASS dump (mimikatz)\nDefense evasion: vssadmin shadow copy deletion\nClean backup: offline tape D-4 (verified restore test passed)",
        hints = mapOf(
            1 to "The kill chain started at an internet-facing appliance — patching is containment for the ROOT cause.",
            2 to "Shadow-copy deletion days before detonation shows premeditation: they planned to block easy recovery.",
            3 to "Never trust a domain joined to a compromised forest — isolate before restoring.",
            4 to "Rebuild from known-good media + offline backup; attach the restored domain only after the forest is clean."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What was the ROOT cause of the incident?",
                options = listOf(
                    "mimikatz running on FILESRV01",
                    "Unpatched internet-facing VPN appliance (CVE-2023-99999)",
                    "Users clicking a phishing email",
                    "vssadmin shadow copy deletion"
                ),
                correctAnswer = 1,
                explanation = "Every later step depended on the appliance foothold. Patch/segment the appliance or the same entry recurs after restore."
            ),
            LabQuestion(
                id = 2,
                question = "Why did the attacker delete shadow copies 24h BEFORE encrypting?",
                options = listOf(
                    "To free disk space for the payload",
                    "Defense evasion + sabotage of the fastest recovery path",
                    "A malfunction in the GPO push",
                    "To avoid triggering antivirus"
                ),
                correctAnswer = 1,
                explanation = "vssadmin delete shadows /all (T1490 Inhibit System Recovery) destroys the quickest restore option and signals deliberate operation."
            ),
            LabQuestion(
                id = 3,
                question = "What is the correct restoration strategy?",
                options = listOf(
                    "Restore the domain controller from the D-1 snapshot",
                    "Rebuild clean infrastructure from verified media, restore data from the D-4 offline tape, keep EDR isolation until forest-wide cleanup",
                    "Pay the ransom for a fast decryptor",
                    "Restore files with an online decryptor found online"
                ),
                correctAnswer = 1,
                explanation = "Assume the whole domain is compromised: rebuild from known-good media, restore from the verified offline backup, and never trust in-place cleaning of a flagged forest."
            ),
            LabQuestion(
                id = 4,
                question = "Which single investment would have most reduced blast radius?",
                options = listOf(
                    "More mailbox licenses",
                    "Immutable, offline, regularly restore-tested backups plus network segmentation of critical shares",
                    "A bigger SIEM retention window",
                    "Weekly phishing simulations"
                ),
                correctAnswer = 1,
                explanation = "Ransomware economics depend on destroying recovery. Immutable off-domain backups + segmentation cap the damage regardless of entry vector."
            ),
            LabQuestion(
                id = 5,
                question = "During response, the CEO asks whether to pay. What is the strongest defensible position?",
                options = listOf(
                    "Pay quickly to minimize downtime — courts expect it",
                    "Never pay; law enforcement discourages payment and there is no guarantee of decryption or non-republication",
                    "Pay only if the CFO approves",
                    "Pay in cryptocurrency to stay anonymous"
                ),
                correctAnswer = 1,
                explanation = "CISA/FBI guidance: payment funds future attacks, offers no guarantee, and marks you as a paying target. A tested recovery plan is the resilient answer."
            )
        ),
        debrief = "Ransomware is a business-continuity attack. Root-cause the entry vector, assume domain compromise, and lean on offline immutable backups — not the decryptor lottery."
    )

    fun threatHuntingLab(): LabContent = LabContent(
        id = 11,
        title = "Lab 11 — Threat Hunting: Living-off-the-Land",
        description = "Hunt a LOTL backdoor that leaves no malware samples — pure hypothesis-driven detection engineering.",
        category = "Threat Hunting",
        difficulty = "Advanced",
        scenario = """
            Threat intel bulletin: APT-style operators are abusing native Windows binaries (LOLBAS) at several peers.
            No malware files, no new services — you must HUNT.

            Available telemetry: Sysmon (Event ID 1 process, 3 network, 7 image load, 10 process access),
            PowerShell 4104 script-block logging, DNS query logs, 90-day retention.

            Environment: 400 Windows endpoints, egress proxy, no baseline anomalies filed in the last quarter.
        """.trimIndent(),
        evidence = "Sysmon available: EID 1/3/7/10\nPowerShell script-block logging: ON\nDNS logs: ON\nRetention: 90 days\nEnvironment: 400 endpoints",
        hints = mapOf(
            1 to "Start from techniques, not IOCs: what do certutil, rundll32 and mshta look like when abused?",
            2 to "Parent-child anomalies beat file signatures — office apps spawning scripting hosts is the classic tell.",
            3 to "Encoded one-liners show up as high-entropy script blocks; prevalence-ranking kills the false positives.",
            4 to "A hunt that doesn't end in a detection rule has to be repeated by hand forever."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "Which hunt hypothesis targets C2-over-DNS most directly?",
                options = listOf(
                    "Endpoints with >50 unique DNS TXT queries per hour to non-resolver domains",
                    "Endpoints running Chrome",
                    "Endpoints with failed Kerberos logons",
                    "Endpoints booting slowly"
                ),
                correctAnswer = 0,
                explanation = "High-volume unique TXT queries to odd domains is the classic DNS-tunneling fingerprint (T1071.004)."
            ),
            LabQuestion(
                id = 2,
                question = "Which Sysmon pairing BEST exposes mshta abuse?",
                options = listOf(
                    "EID 1 where ParentImage is office/winword/excel and Image ends in mshta.exe",
                    "EID 7 alone (image loads)",
                    "EID 10 alone (process access)",
                    "EID 3 where DestinationPort is 443"
                ),
                correctAnswer = 0,
                explanation = "Documents spawning scripting hosts is the LOLBAS tell; parent-child context beats single-event rules."
            ),
            LabQuestion(
                id = 3,
                question = "PowerShell 4104 shows a base64 blob decoded to a downloader. What first?",
                options = listOf(
                    "Delete the event log",
                    "Pivot on the URL + process GUID: what else did that process and user touch in 90 days?",
                    "Email all 400 users about PowerShell",
                    "Reimage the endpoint immediately"
                ),
                correctAnswer = 1,
                explanation = "Scope before acting: pivot on actor, process GUID and infrastructure across the full retention window to size the intrusion."
            ),
            LabQuestion(
                id = 4,
                question = "How do you kill false positives at fleet scale?",
                options = listOf(
                    "Alert on every rundll32 execution",
                    "Prevalence-rank: alert only on behaviors seen on <1% of endpoints, rare parents, or never-before-seen pairs",
                    "Increase SIEM retention",
                    "Turn on verbose logging only"
                ),
                correctAnswer = 1,
                explanation = "Prevalence-ranking is the core hunting primitive: rare behavior on your fleet is the signal."
            ),
            LabQuestion(
                id = 5,
                question = "What is the correct END product of a mature hunt?",
                options = listOf(
                    "A spreadsheet of findings",
                    "Converted detections: analytic rules deployed to the SIEM/EDR + scheduled hunts for what cannot be automated",
                    "A threat intel report",
                    "A new EDR agent rollout"
                ),
                correctAnswer = 1,
                explanation = "Hunts graduate into standing detections; the hunt program closes the loop back into detection engineering."
            )
        ),
        debrief = "Hunting is hypothesis-driven detection engineering: techniques over IOCs, prevalence over signatures, and every hunt ends as a rule."
    )

    fun webExploitationLab(): LabContent = LabContent(
        id = 12,
        title = "Lab 12 — Web Exploitation: SQLi to Shell",
        description = "Ethical-hacking chain: from a vulnerable parameter to SQL injection, data access and privilege escalation — on an authorized range.",
        category = "Ethical Hacking",
        difficulty = "Intermediate",
        scenario = """
        AUTHORIZED ENVIRONMENT ONLY — this is the company's own training range.

        Target: http://10.10.10.22 (Acme inventory portal)
        Recon findings:
        - /product?id=31 reflects the id parameter into an SQL error
        - Login form at /login responds differently to ' OR '1'='1
        - App runs as db_user with WRITE privileges on the orders schema
        - MySQL 8.0.36, error-based feedback available
        """.trimIndent(),
        evidence = "Injection point: /product?id=\nDB: MySQL 8.0.36, error-based\nDB user: db_user (SELECT+INSERT on orders)\nLogin form: classic bypass candidate\nScope: authorized range only",
        hints = mapOf(
            1 to "Error-based union queries reveal structure fast: order by N until the error changes.",
            2 to "information_schema is your map: enumerate tables/columns before extracting data.",
            3 to "The login bypass proves the same sink is reachable from a second parameter — inputs share the backend.",
            4 to "Writing files requires FILE privilege AND a known webroot; check both before assuming RCE."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "First payload to confirm and count columns?",
                options = listOf(
                    "' UNION SELECT NULL-- - incrementing NULLs until the error clears",
                    "'; DROP TABLE orders-- -",
                    "admin'--",
                    "<script>alert(1)</script>"
                ),
                correctAnswer = 0,
                explanation = "UNION SELECT NULL enumeration is the safe, standard column-count probe; destructive payloads violate authorization scope."
            ),
            LabQuestion(
                id = 2,
                question = "Which query lists all tables in the current database?",
                options = listOf(
                    "' UNION SELECT table_name, NULL FROM information_schema.tables WHERE table_schema=database()-- -",
                    "SHOW ME THE TABLES-- -",
                    "' OR 1=1-- -",
                    "SELECT * FROM users-- -"
                ),
                correctAnswer = 0,
                explanation = "information_schema.tables scoped to database() enumerates the schema in one round-trip."
            ),
            LabQuestion(
                id = 3,
                question = "The login bypass works. Why is it still a HIGH finding, not critical-by-itself?",
                options = listOf(
                    "Because it is authentication bypass only — impact depends on what the account can reach (then chaining to the WRITE-privileged injection raises severity)",
                    "Because SQLi is always low",
                    "Because MySQL 8 is unaffected",
                    "Because the app is internal only"
                ),
                correctAnswer = 0,
                explanation = "Severity follows impact: a bypass into a low-privilege portal is high; the same sink with write privileges and FILE rights escalates toward critical."
            ),
            LabQuestion(
                id = 4,
                question = "Which remediation set ACTUALLY fixes the class of bug?",
                options = listOf(
                    "Block the word SELECT in a WAF",
                    "Parameterized queries/prepared statements everywhere + least-privilege DB account + error suppression",
                    "Base64-encode the id parameter",
                    "Move the app to HTTPS"
                ),
                correctAnswer = 1,
                explanation = "WAFs are bypassable speed bumps. The durable fix is parameterization at every sink, least-privilege service accounts, and no SQL errors to clients."
            ),
            LabQuestion(
                id = 5,
                question = "During the engagement you find customer PII in a table. What does the RULES OF ENGAGEMENT dictate?",
                options = listOf(
                    "Download everything as proof",
                    "Stop, document the finding without exfiltrating personal data, and notify the engagement owner",
                    "Post a screenshot to the team chat",
                    "Keep testing to find more PII"
                ),
                correctAnswer = 1,
                explanation = "Proof-of-concept, not exfiltration: authorized testing never bulk-copies personal data — document, notify, and let the owner remediate."
            )
        ),
        debrief = "Web exploitation chains small findings into big impact. Scope and discipline are what separate professionals from script kiddies."
    )

    fun malwareSandboxLab(): LabContent = LabContent(
        id = 13,
        title = "Lab 13 — Malware Triage: Static to Sandboxed Detonation",
        description = "Triage an unknown binary like a malware analyst: hashes, strings, imports, sandbox behavior and reporting.",
        category = "Malware Analysis",
        difficulty = "Intermediate",
        scenario = """
        Mail gateway quarantined invoice_apr.exe (1.2 MB) addressed to accounting.

        You have: an isolated analysis VM (snapshot reverted after use), a Windows 10 VM,
        a REMnux VM, INetSim, and internet access THROUGH the lab proxy only.

        Initial observations:
        - PE32 executable, compiled timestamp 2024-02-29 03:41:22 UTC
        - Sections: .text, .rdata, .rsrc, .UPX0 (!), .UPX1 (!)
        - Strings (pre-unpack): "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "hxxp://update-cdn[.]services-support[.]top/payload"
        - No valid code signature
        """.trimIndent(),
        evidence = "File: invoice_apr.exe (PE32, UPX-packed)\nStrings: Run-key persistence, C2 hxxp://update-cdn[.]services-support[.]top\nNo valid signature\nCompile stamp: 2024-02-29 (timestomping suspect)",
        hints = mapOf(
            1 to "UPX sections mean the interesting strings are hidden — unpack first (upx -d), then re-analyze.",
            2 to "Run-key + C2 URL = persistence and command & control: two solid behavioral IOCs already.",
            3 to "The compile timestamp claims Feb 29 — a date that only exists in leap years. Check against the PE header characteristics.",
            4 to "Network IOCs belong in the blocklist AND the report; host IOCs go to EDR hunt queries."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "What is the correct triage ORDER?",
                options = listOf(
                    "Detonate on production-adjacent host → strings → hash",
                    "Hash + static analysis → unpack → sandboxed detonation with INetSim → behavioral IOCs → report",
                    "Upload to VirusTotal → done",
                    "Run it and see what breaks"
                ),
                correctAnswer = 1,
                explanation = "Static-first (cheap, safe), unpack, then controlled detonation in an isolated, instrumented environment — never production."
            ),
            LabQuestion(
                id = 2,
                question = "The .UPX0/.UPX1 sections indicate what?",
                options = listOf(
                    "The binary is digitally signed",
                    "The payload is packed; static strings are untrustworthy until unpacked",
                    "The file is corrupted",
                    "It is a .NET assembly"
                ),
                correctAnswer = 1,
                explanation = "UPX packing hides strings/imports. Unpack (upx -d) or detonate to observe the real behavior."
            ),
            LabQuestion(
                id = 3,
                question = "The Run-key string + C2 URL give you which two IOCs?",
                options = listOf(
                    "Persistence (HKCU Run key) and command & control domain",
                    "Lateral movement and privilege escalation",
                    "Exfiltration and impact",
                    "Initial access and discovery"
                ),
                correctAnswer = 0,
                explanation = "Run-key = T1060/T1547.001 persistence; the update-cdn domain is the C2 channel (T1071). Both are immediately hunt-able."
            ),
            LabQuestion(
                id = 4,
                question = "Why is the compile timestamp suspicious?",
                options = listOf(
                    "It is too recent",
                    "2024-02-29 is a leap date often faked; timestomping misleads attribution (T1070.006)",
                    "Timestamps are always UTC",
                    "PE headers never contain timestamps"
                ),
                correctAnswer = 1,
                explanation = "Attackers fake compile times to misdirect analysts; cross-check with first-seen dates and certificate/signature data."
            ),
            LabQuestion(
                id = 5,
                question = "What belongs in the FINAL malware report?",
                options = listOf(
                    "Only the VirusTotal link",
                    "Executive summary, behavioral IOCs (hashes, C2, registry), MITRE mapping, detection recommendations (YARA/Sigma/EDR), and containment steps",
                    "A memory dump",
                    "The unpacked binary attached to email"
                ),
                correctAnswer = 1,
                explanation = "Reports drive action: IOCs to block, detections to deploy, MITRE context for the IR team, and clear containment guidance."
            )
        ),
        debrief = "Malware triage is a pipeline: hash, unpack, detonate safely, extract IOCs, map to ATT&CK, and turn everything into detections."
    )

    fun digitalForensicsLab(): LabContent = LabContent(
        id = 14,
        title = "Lab 14 — Digital Forensics: The Exfiltrated Archive",
        description = "Build a defensible timeline from disk artifacts: registry, prefetch, MFT and browser history.",
        category = "Digital Forensics",
        difficulty = "Advanced",
        scenario = """
        Legal hold: suspect laptop of a departing engineer who downloaded the customer database.

        You are handed a forensic IMAGE (never the live machine) plus:
        - Registry hives (SYSTEM, SOFTWARE, SAM, NTUSER.DAT)
        - C:\\Windows\\Prefetch
        - $MFT and USN journal
        - Chrome/Edge history and downloads
        - USBSTOR registry artifacts

        Known facts: DLP flagged a 6 GB 7z archive named clients_master.7z at 23:41 on Mar 14.
        """.trimIndent(),
        evidence = "Artifact set: registry hives, Prefetch, $MFT, USN, browser history, USBSTOR\nDLP flag: clients_master.7z, 6 GB, 23:41 Mar 14\nMedia: forensic image (chain of custody logged)",
        hints = mapOf(
            1 to "Shimcache/AmCache and Prefetch give you program EXECUTION with timestamps — build the spine of the timeline there.",
            2 to "USBSTOR + setupapi.dev.log answers 'was removable media involved' in minutes.",
            3 to "$MFT standard information vs $FILE_NAME attributes can reveal timestomping discrepancies.",
            4 to "A forensic timeline needs SOURCE attribution — every event should cite its artifact."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "Why analyze the forensic image rather than the live laptop?",
                options = listOf(
                    "Live systems are faster to search",
                    "Imaging preserves evidentiary integrity (hashes + chain of custody); live triage alters artifacts",
                    "Registry tools don't work live",
                    "Images compress better"
                ),
                correctAnswer = 1,
                explanation = "Acquisition-first forensics protects admissibility: hash the image, work from copies, document every step."
            ),
            LabQuestion(
                id = 2,
                question = "Which artifact BEST answers 'was a USB drive attached, and which one'?",
                options = listOf(
                    "Chrome history",
                    "USBSTOR registry key + setupapi.dev.log (vendor/serial/timestamps)",
                    "Prefetch of explorer.exe",
                    "NTUSER.DAT RunMRU"
                ),
                correctAnswer = 1,
                explanation = "USBSTOR enumerates storage devices with serial numbers; setupapi.dev.log adds install timestamps — the removable-media timeline."
            ),
            LabQuestion(
                id = 3,
                question = "7z.exe appears in Prefetch at 23:38, DLP flags the archive at 23:41. What does this support?",
                options = listOf(
                    "The user only browsed files",
                    "Execution 3 minutes before exfiltration: intent + action timeline (execution → archive → transfer)",
                    "Prefetch is unreliable and should be ignored",
                    "The DLP system was wrong"
                ),
                correctAnswer = 1,
                explanation = "Correlated artifacts build the narrative: tool execution, artifact creation, then transfer — three sources agreeing on sequence."
            ),
            LabQuestion(
                id = 4,
                question = "$FILE_NAME says file created 09:00, $STANDARD_INFORMATION says 03:00. Likely explanation?",
                options = listOf(
                    "Clock drift, no action needed",
                    "Timestomping: $SI timestamps were manipulated (T1070.006) — corroborate with $MFT/USN journals",
                    "The MFT is corrupt",
                    "Timezone mismatch only"
                ),
                correctAnswer = 1,
                explanation = "$SI vs $FN divergence is the classic timestomping signature; journals and logs provide ground truth."
            ),
            LabQuestion(
                id = 5,
                question = "What makes your timeline DEFENSIBLE in a hearing?",
                options = listOf(
                    "It is detailed and long",
                    "Every event cites its source artifact, acquisition was hashed and documented, and tools/methods are reproducible",
                    "It was made with expensive tools",
                    "It only includes the conclusion"
                ),
                correctAnswer = 1,
                explanation = "Admissibility = provenance: hashed acquisition, documented chain of custody, source-attributed events, reproducible methodology."
            )
        ),
        debrief = "Forensics is a chain of attributed facts. Images, hashes, source-cited events and reproducible methods make findings survive cross-examination."
    )

    fun activeDefenseLab(): LabContent = LabContent(
        id = 15,
        title = "Lab 15 — Active Defense & Detection Engineering",
        description = "Turn every prior lab into standing detections: canary tokens, honeypots, high-signal alert design.",
        category = "Blue Team",
        difficulty = "Intermediate",
        scenario = """
        You own detection for a 400-endpoint fleet after THREE incidents this quarter:
        the consent-phish (Lab 09), the ransomware drill (Lab 10) and the LOLBAS hunt (Lab 11).

        Budget: you can deploy canary tokens, one honeypot server, Sysmon+config update,
        and 5 high-signal detection rules in the SIEM. Everything must map to ATT&CK.
        """.trimIndent(),
        evidence = "Deployable: canary tokens, 1 honeypot, Sysmon config update, 5 SIEM rules\nThreats seen: OAuth consent abuse, LSASS dump + shadow-delete, LOLBAS C2\nRequirement: every detection maps to ATT&CK",
        hints = mapOf(
            1 to "Canary tokens have a false-positive rate near zero — spend them where intruders must go (creds, shares, cloud tokens).",
            2 to "A honeypot pretending to be a file server catches lateral movement that notification-based controls miss.",
            3 to "Each of the 5 rules should target ONE high-value technique, not a broad behavior.",
            4 to "Detection quality = alert-to-incident ratio; if it pages and isn't actionable, it isn't a detection, it's noise."
        ),
        questions = listOf(
            LabQuestion(
                id = 1,
                question = "Where do canary tokens deliver the HIGHEST signal?",
                options = listOf(
                    "On every desktop shortcut",
                    "Fake credentials in a password manager note, a token on the finance share, and a fake AWS key in a config file",
                    "In the SIEM itself",
                    "In email signatures"
                ),
                correctAnswer = 1,
                explanation = "Only an intruder with hands on the keys touches them: near-zero false positives, direct evidence of hands-on-keyboard (T1552/T1530)."
            ),
            LabQuestion(
                id = 2,
                question = "Which honeypot placement catches the MOST realistic attacker?",
                options = listOf(
                    "On the internet edge with default banners",
                    "Inside the LAN, dressed as FILESRV02 with a share named 'Backups', monitored for any access",
                    "In the DMZ next to the VPN",
                    "On the CEO's laptop"
                ),
                correctAnswer = 1,
                explanation = "Lateral movement targets file servers; anything touching a system that should never be accessed is high-signal (T1021)."
            ),
            LabQuestion(
                id = 3,
                question = "Given 5 rules, which set is BEST?",
                options = listOf(
                    "One rule per MITRE tactic (14 broad rules)",
                    "vssadmin delete shadows (T1490), LSASS handle access (T1003.001), office→script-host spawn (T1059), new inbox forwarding rule (T1114.003), OAuth consent to unverified app (T1528)",
                    "Five variants of 'failed logon spike'",
                    "One rule per endpoint vendor"
                ),
                correctAnswer = 1,
                explanation = "Each rule is specific, maps to a technique from real incidents in this environment, and pages only on high-fidelity behaviors."
            ),
            LabQuestion(
                id = 4,
                question = "After 2 weeks the forwarding-rule rule fired 300 times, all legitimate HR automation. Action?",
                options = listOf(
                    "Disable the rule",
                    "Tune: exclude the HR service account + scope to user-created rules; document the exception",
                    "Create 300 more rules",
                    "Ignore the alerts"
                ),
                correctAnswer = 1,
                explanation = "Detection engineering is a loop: tune with scoped exclusions and documentation, never silence a true-positive class."
            ),
            LabQuestion(
                id = 5,
                question = "What is the strongest ARGUMENT for this program's budget?",
                options = listOf(
                    "Compliance requires controls",
                    "Each control maps to an observed technique from a real incident this quarter, with measured alert fidelity",
                    "Competitors have honeypots",
                    "The SIEM is already paid for"
                ),
                correctAnswer = 1,
                explanation = "Risk-driven detection grounded in observed adversary behavior + fidelity metrics is the language leadership funds."
            )
        ),
        debrief = "Active defense = deception (canaries, honeypots) + precision detections mapped to ATT&CK, tuned by measured fidelity — not more noise."
    )
}

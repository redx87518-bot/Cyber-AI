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
        socInvestigationLab()
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
}

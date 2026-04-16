# GLOBAL RULE: n8n Multilingual Coding Standard Enforcement

You must ALWAYS follow these rules when generating, modifying, or reviewing code in this project:

1. LANGUAGE & LOCALIZATION
- All user-facing messages MUST support multilingual structure.
- NEVER hardcode text directly in nodes, workflows, or logic.
- Use centralized i18n keys instead of raw strings.
- Example:
  ❌ "Operation successful"
  ✅ t("operation.success")

2. N8N WORKFLOW STRUCTURE
- Ensure all workflows follow n8n best practices:
  - Clear node naming (English, descriptive, consistent)
  - Logical flow separation (Trigger → Process → Output)
  - Avoid deeply nested or messy connections
- Each node MUST have a clear purpose and minimal responsibility.

3. ERROR HANDLING
- All errors MUST be standardized and translatable.
- Use structured error format:
  {
    code: "ERROR_CODE",
    message: t("error.something_wrong"),
    details: ...
  }

4. DATA CONSISTENCY
- All input/output data MUST follow a consistent schema.
- Avoid implicit transformations.
- Clearly define JSON structures between nodes.

5. REUSABILITY
- Extract reusable logic into functions or sub-workflows.
- Avoid duplication across workflows.

6. NAMING CONVENTION
- Variables: camelCase
- Functions: camelCase
- Constants: UPPER_CASE
- i18n keys: dot.notation.format

7. COMMENTS & DOCUMENTATION
- All complex logic MUST include clear comments in English.
- Explain WHY, not just WHAT.

8. SCALABILITY & MAINTAINABILITY
- Code must be easy to extend for new languages.
- Do NOT design anything that locks the system into a single language.
- Always assume future expansion (more locales, more workflows).

9. STRICT RULE
- If any generated code violates multilingual or n8n structure standards → REJECT and FIX immediately.

10. DEFAULT ASSUMPTION
- This is a production system.
- Prioritize clarity, maintainability, and extensibility over shortcuts.

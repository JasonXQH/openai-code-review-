curl "https://api.openai.com/v1/chat/completions" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer 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" \
    -d '{
        "model": "gpt-4o-mini",
        "messages": [
            {
                "role": "system",
                "content": "You are a helpful assistant."
            },
            {
                "role": "user",
                "content": "和我说一下Java是一门什么样的语言，用中文"
            }
        ]
    }'
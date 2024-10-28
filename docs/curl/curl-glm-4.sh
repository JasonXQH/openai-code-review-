curl -X POST \
        -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiZGZhODMzOGMwM2Q3M2Y3YzMyMmI3ZDk5YTQzZGNjOTEiLCJleHAiOjE3MzAwOTE4MzIzODIsInRpbWVzdGFtcCI6MTczMDA5MDAzMjM4Nn0.djCZHlL_HMWdylpStops6vQKCzqmF7MI_I5NkOtzNcg" \
        -H "Content-Type: application/json" \
        -H "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)" \
        -d '{
          "model":"glm-4",
          "stream": "true",
          "messages": [
              {
                  "role": "user",
                  "content": "1+1"
              }
          ]
        }' \
  https://open.bigmodel.cn/api/paas/v4/chat/completions
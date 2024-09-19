from fastapi import FastAPI, WebSocket
from fastapi.responses import HTMLResponse

import openai

app = FastAPI()

@app.get("/", response_class=HTMLResponse)
async def root():
    with open('websocat.html', 'r') as file:
        page = f"""{file.read()}"""
        print(page)
        return page

@app.websocket("/ws")
async def websocket_endpoint(websocket: WebSocket):
    await websocket.accept()
    while True:
        data = await websocket.receive_text()

        # test
        print(f"Получено сообщение: {data}")
        await websocket.send_text(f"Сообщение получено: {data}") 

        # подключаем api
        openai.api_key = "API-ключ"

        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            message=[
                {"role": "user", "content": data}
            ]
        )

        output = response.choices[0].message["content"]

        # output
        await websocket.send_text(f"Ответ: {output}") 
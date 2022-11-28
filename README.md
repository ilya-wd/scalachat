The program allows users to chat with each other. It is a pretty simple chat – a user can input their name and message only.

A user can share chat id with other users so they can connect to their chat room. By default, the id is generated automatically. However, a user can also create a custom name for their chat.

The MinimalApplication object is responsible for the main functioning of ScalaChat. It establishes WebSocket connections, which allows the chat to be updated in real time, renders the web page, and it is where Chat objects are generated and stored.

It is a seemingly simple project. The main problem was to make Scala Cask work, as the information available on the Internet about it is very scarce.
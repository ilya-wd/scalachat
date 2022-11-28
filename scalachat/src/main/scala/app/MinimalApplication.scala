package app
//import app.MinimalApplication34.messages
import scalatags.Text.all._

import java.util.Calendar

// IMPORTANT NOTE: the port that worked for me is 8080.

class Chat(val tag: String = Calendar.getInstance().getTimeInMillis.toString) {
  def thisTag = this.tag

  var openConnections = Set.empty[cask.WsChannelActor]

  def messageList() = {
    frag(
      for((name, msg) <- messages)
      yield p(b(name), " ", msg)
    )
  }
    var messages = Vector[(String,String)](
  )
}

object MinimalApplication34 extends cask.MainRoutes{

  var openEchoConnections = Set.empty[cask.WsChannelActor]

  var openChats = Map.empty[String, Chat]

  var value = ""

  @cask.get("/user")
  def showUserProfile() = {
    html(
      head(),
      body(
        div(cls := "container")(
          h1("Scala Chat!"),
          hr,
        ),
      )
    )
  }


  @cask.websocket("/subscribe/:idChat")
  def subscribe(idChat: String): cask.WebsocketResult = {
    if (openChats.contains(idChat)) {
      val chat = openChats(idChat)
      cask.WsHandler { connection =>
        cask.WsActor {
          case cask.Ws.Text(msg) =>
            if (msg.toInt < chat.messages.length){
              connection.send(
                cask.Ws.Text(
                  ujson.Obj("index" -> chat.messages.length, "txt" -> chat.messageList().render).render()
                )
              )
            }else{
              chat.openConnections += connection
            }
          case cask.Ws.Close(_, _) => chat.openConnections -= connection
        }
      }
    } else {
      cask.Response("", statusCode = 403)
    }
  }

  @cask.get("/")
  def hello(chatID: String = ""): cask.model.Response.Raw = {
    if (!openChats.contains(chatID)) {
      var newChat = if (chatID != "") new Chat(chatID) else new Chat()
      openChats += (newChat.thisTag -> newChat)
      cask.Redirect(s"/?chatID=${newChat.thisTag}")
    } else {
      var chat = openChats(chatID)


      html(
        head(
          link(
            rel := "stylesheet",
            href := "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
          ),
          script(raw(raw"""
            function submitForm(){
              fetch(
                "/",
                {
                  method: "POST",
                  body: JSON.stringify({name: nameInput.value, msg: msgInput.value, chatID: "${chat.thisTag}"})
                }
              ).then(response => response.json())
               .then(json => {
                if (json.success) {
                  messageList.innerHTML = json.txt
                  msgInput.value = ""
                  errorDiv.innerText = ""
                } else {
                  errorDiv.innerText = json.txt
                }
              })
            }

            var socket = new WebSocket("ws://" + location.host + "/subscribe/${chat.thisTag}");
            socket.onopen = function(ev){ socket.send("0") }
            socket.onmessage = function(ev){
              var json = JSON.parse(ev.data)
              messageList.innerHTML = json.txt
              socket.send("" + json.index)
            }
          """)
        ),
        body(
          div(cls := "container")(
            h1(s"Welcome to Scala Chat!"),
            hr,
            h2(s"Chat id: ${chat.thisTag}"),
            hr,
            h3(s"Use the following address to connect to this chat: http://localhost:8080/?chatID=${chat.thisTag}"),
            a(
              target:= "_blank",
              href:= s"http://localhost:8080/?chatID=${chat.thisTag}",
              "... Or simply click here"
            ),
            hr,
            a(
              target:= "_blank",
              href:= s"http://localhost:8080/?chatID=${val anotherChat = new Chat();
                openChats += (anotherChat.thisTag -> anotherChat)
              anotherChat.thisTag}",
              "Or you can use this link to create a new chat"
            ),
            hr,
            div(id := "messageList")(
            ),
            hr,
            form(action := "/",
                 target := "_blank")(
              input(
                `type` := "text",
                name := "chatID",
                placeholder := "Write the id of your chat here",
                width := "80%"
              ),
              input(
                `type` := "submit",
                width := "20%"
              )

            ),
            hr,
            div(id := "errorDiv", color.red),
            form(onsubmit := "submitForm(); return false")(
              input(
                `type` := "text",
                id := "nameInput",
                placeholder := "User name",
                width := "20%"
              ),
              input(
                `type` := "text",
                id := "msgInput",
                placeholder := "Please write a message!",
                width := "60%"
              ),
              input(`type` := "submit", width := "20%")
            )
          )
        )
       )
      )
    }
  }

  @cask.postJson("/")
  def postHello(name: String, msg: String, chatID: String) = {

    if (name == "") ujson.Obj("success" -> false, "txt" -> "Name cannot be empty")
    else if (name.length >= 10) ujson.Obj("success" -> false, "txt" -> "Name cannot be longer than 10 characters")
    else if (msg == "") ujson.Obj("success" -> false, "txt" -> "Message cannot be empty")
    else if (msg.length >= 160) ujson.Obj("success" -> false, "txt" -> "Message cannot be longer than 160 characters")
    else if (openChats.contains(chatID)) {

      var chat = openChats(chatID)

      chat.messages = chat.messages :+ (name -> msg)
      val notification = cask.Ws.Text(
        ujson.Obj("index" -> chat.messages.length, "txt" -> chat.messageList().render).render()
      )
      // for loop goes thru all the connections and sends the notification
      for(conn <- chat.openConnections) conn.send(notification)
//      chat.openConnections = Set.empty
      ujson.Obj("success" -> true, "txt" -> chat.messageList().render)
    } else ujson.Obj("success" -> false, "txt" -> "Couldn't find your chat")
  }
  initialize()
}
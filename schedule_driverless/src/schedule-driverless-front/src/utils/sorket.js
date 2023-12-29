import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'


let sorketData = {
	headers: null,
	getBookUrl: '',
	getSendUrl: '',
	timer: null,
}

function getBookUrl(val) {
	sorketData.getBookUrl = "/user/queue/" + val
}

function getSendUrl(val) {
	sorketData.getSendUrl = "/app/" + val
}

/** 变更发送参数 **/
export function changeSendData(stompObj, roadId) {
	sorketData.sendData = { roadId: roadId }
	getData(stompObj)
}

/** 连接websocket服务端 **/
// stompObj用于存放实例，type区分订阅服务的参数，sendData订阅用的参数，callback回调函数
export function connection(stompObj, type, sendData, callback) {
  let that = this
	sorketData.sendData = sendData
  // 建立连接对象
  let url = window._CONFIG['domianURL'] + "/newsWebsocket";
  let socket = new SockJS(url);
  // 获取STOMP子协议的客户端对象
  stompObj.stompClient = Stomp.over(socket);
  let token = Vue.ls.get(ACCESS_TOKEN);
  // 定义客户端的认证信息,按需求配置
  sorketData.headers = {
    token: token
  }
	// 获取订阅链接与发送链接
	getBookUrl(type)
	getSendUrl(type)
  // 向服务器发起websocket连接
  stompObj.stompClient.connect(sorketData.headers, () => {
		console.log('websocket连接成功')
    bookServe(stompObj, (res) => {
			callback(res)
		}) // 订阅服务端  后台会在这返回数据（数据处理）
    getData(stompObj) // 发请求到后台获取数据

  }, (err) => {
    // 连接发生错误时的处理函数
    console.log('失败')
    console.log(err);
  });
}

/** 断开连接 **/
export function disConnection(stompObj, callback) {
	// 向服务器断开websocket连接
	stompObj.stompClient.disconnect(function() {
    // alert("websocket连接断开!");
	})
}

/** 订阅消息 **/
function bookServe(stompObj, callback) {
  stompObj.subscribeObj = stompObj.stompClient.subscribe(sorketData.getBookUrl, (msg) => { // 订阅服务端提供的某个topic
    // console.log(msg);  // msg.body存放的是服务端发送给我们的信息
		// console.log('订阅服务端成功')
    let tmpArr = JSON.parse(msg.body)
		if(sorketData.timer){
			clearInterval(sorketData.timer)
		}
    sorketData.timer = setInterval(() => {
      getData(stompObj)
			clearInterval(sorketData.timer)
    }, 10 * 1000)
		console.log('返回数据:',tmpArr)
		callback(tmpArr)
  }, sorketData.headers);
}

/** 取消订阅 **/
export function unsubscribe(stompObj) {
	// 取消订阅
	stompObj.subscribeObj.unsubscribe()
}

/** 发送数据至已订阅的路径 **/
function getData(stompObj) {
	// console.log('前端发送请求至后台成功')
  // 前端发送请求至后台 后台返回新得数据
  stompObj.stompClient.send(sorketData.getSendUrl,
    sorketData.headers,
    JSON.stringify(sorketData.sendData),
  ) //用户加入接口
}
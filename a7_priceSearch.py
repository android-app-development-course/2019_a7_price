import requests
import socket
import threading
import os
import time
from selenium import webdriver
from PIL import Image

driver=webdriver.PhantomJS(executable_path=r"C:\Users\Administrator\Desktop\phantomjs.exe")
def get_target_url(url):
    driver.get("http://www.hisprice.cn/")
    driver.find_element_by_id("kValId").send_keys(url)
    driver.find_element_by_css_selector("button").click()
    time.sleep(1)
def get_title():
    try:
        title=driver.find_element_by_id("titleId").text
        print(title)
        if title=="":
            title="获取标题失败!"
        return title
    except:
        return "获取标题失败！"
def get_price():
    try:
        price=driver.find_element_by_id("minMaxDivId").text
        print(price)
        if price=="":
            price="获取最低最高价格失败!"
        return price
    except:
        return "获取最低最高价格失败！"
def get_snap(driver,file_path):  # 对目标网页进行截屏,这里截的是全屏
    driver.save_screenshot(file_path)
    page_snap_obj=Image.open(file_path)
    return page_snap_obj
 
 
def get_image(driver,file_path): # 对价格变化图所在位置进行定位，然后截取验证码图片
    img=driver.find_element_by_id("container")
    time.sleep(2)
    location = img.location
    print(location)
    size = img.size
    left = location['x']
    top = location['y']
    right = left + size['width']
    bottom = top + size['height']
 
    page_snap_obj = get_snap(driver,file_path)
    image_obj = page_snap_obj.crop((left, top, right, bottom))
    image_obj.save(file_path)
    return image_obj  # 得到价格变化图
def handle_client(client_socket, client_id):
  """处理客户端请求"""
  # 获取客户端请求数据
  while True:
    try:
      request_data = client_socket.recv(1024)
    except Exception:
      time.sleep(0.2)
      continue
    if len(request_data) > 0:
        #保存价格变化图
      data_url=str(request_data,encoding="utf_8")
      now_time = time.strftime("%d-%H-%M-%S", time.localtime(time.time()))
      file_path=r"C:\Users\Administrator\Desktop\price_Search\picture./"+now_time +".png"
      get_target_url(data_url)
      get_image(driver,file_path)
      # 将值传入对象
      try:
          length=os.path.getsize(file_path)
          st=str(length)
          title=get_title()
          price=get_price()
          fst=title+"^"+price+"^"+st
          client_socket.send(bytes(fst,"utf-8"))
          time.sleep(1)
          try:
            data=client_socket.recv(1024)
            if data==b"ok":
              fp=open(file_path,"rb")
              while True:
                data=fp.read(1024)
                if not data:
                  print("send all")
                  break
                client_socket.send(data)
              client_socket.close()
            else:
              print("have no data")
          except:
              print("发送失败")
              st0="no"
              dt0= bytes(st0, encoding="utf8")
              client_socket.sendall(dt0)
              break
          
      except:
          st0="no"
          dt0= bytes(st0, encoding="utf8")
          client_socket.sendall(dt0)
          print("凉了,该商品没有被收录")
          break
          
hostname = socket.gethostname()
host = socket.gethostbyname(hostname)
port = 8000
BUFSIZE = 1024
ADDR = (host, port)
if __name__ == "__main__":
  server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  """
  socket（）是一个函数，创建一个套接字，
  AF_INET 表示用IPV4地址族，
  SOCK_STREAM 是说是要是用流式套接字
  """
  # server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1) # 设置地址重用
  server_socket.bind(ADDR) # 绑定端口
  server_socket.listen(2) # 开启监听
  client_socket_list = []
  client_num = 0
  Isready = False
 
  while True:
    client_id = client_num
    client_socket, client_address = server_socket.accept()
    print("[%s, %s]用户连接上了" % client_address)
    handle_client_thread = threading.Thread(target=handle_client, args=(client_socket, client_id))
    """
    tartget表示这个进程到底要执行什么行为
    args是target要接受的参数
    """
    client_socket_list.append(client_socket)
    client_num += 1
    client_socket.setblocking(0)
    handle_client_thread.start()

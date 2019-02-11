# from pusher_push_notifications import PushNotifications
# import pyrebase
# config = {
#     'apiKey': "AIzaSyB_KcOFHvE1g1IZf8QGDo7EQUcZYE-rV-U",
#     'authDomain': "smartdoor-7d0e6.firebaseapp.com",
#     'databaseURL': "https://smartdoor-7d0e6.firebaseio.com",
#     'projectId': "smartdoor-7d0e6",
#     'storageBucket': "smartdoor-7d0e6.appspot.com",
#     'messagingSenderId': "554694664421"
#   }
#
# firebase = pyrebase.initialize_app(config)
# db=firebase.database()
#     pn_client = PushNotifications(
#     instance_id='d2691001-bfee-4597-abc6-f470c5392219',
#     secret_key='C87C3ADA6C4ECDC29C995CC47B2F459F246DEAA89125290A679D66F915D8D425',
# )
#
#     def stream_handler(message):
#         print(message)
#
#     my_stream = db.child("DoorA_Status").stream(stream_handler, None)

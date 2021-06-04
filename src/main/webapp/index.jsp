<html>
<body>
<h2>Hello World!</h2>

springMVC 上传文件
<form name="form1" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" name="springMVC上传文件" />
</form>


富文本 上传文件
<form name="form1" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" name="富文本上传文件" />
</form>

</body>
</html>

# My-Base-Kotlin
#guide to convert keystore to basse 64
cd C:\Users\TenUser\Documents

Chuyển đổi file keystore thành chuỗi Base64
certutil -encode keystore.jks keystore.b64
2.xem nội dung
type keystore.b64
3 open with  notepad
notepad keystore.b64
4 delete the file
del keystore.b64
#push multiple repository

git remote -v
origin  https://github.com/your-user/your-repo.git (fetch)
origin  https://github.com/your-user/your-repo.git (push)

git remote add gitlab https://gitlab.com/doanvanquang146/eventdemo.git

git push origin main  # Push lên GitHub
git push gitlab main  # Push lên GitLab


-----------
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0  # Đẩy tag lên GitHub
git push gitlab v1.0.0  # Đẩy tag lên GitLab

git push origin --tags
git push gitlab --tags

--------- clone full repository
 =>>New folder : git clone --mirror <URL_origin>
=>> open clone folder : git remote add gitlab <URL_gitlab>
=>> git push --mirror gitlab
 



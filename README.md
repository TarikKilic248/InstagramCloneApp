My Travel Nootebook Mobil Uygulama Raporu

Bu uygulama kullanıcıların fotoğraf paylaşabildiği, gönderileri beğenebildiği ve yorum yapabildiği bir sosyal media platformudur. FireBase ile çalışmaktadır. 

I. Giriş:

Giriş ekranında Kayıt ol ve Giriş yap düğmesi yer almaktadır.

Kayıt ol:
	İlk defa kayıt olacak kullanıcılar için tasarlanmıştır.
	Basıldığında Kullanıcıyı Firebase’e ekler ve FeedActivity geçiş yapar

Giriş yap butonu
	Email ve Password ile Firebase’de eşleşen bir kullanıcı varsa başarılı şekilde giriş yapar
	Basıldığında FeedActivity’e geçiş yapar
	Eğer Email ve Password boş ise Toast mesajı çıkar.


II. Ana Ekran(FeedActivity):

Burada Firebase’deki bütün kullanıcıların yaptığı Post’lar gözükmektedir. Önce Firebase’de kaydedilmiş tüm postları görüntüler. Akış RecyclerView ile yapılmıştır. Görselin sağ tarafı RecyclerRow’da gösterilecek Post yer almaktadır. Ayrıca burada menu de kullanılmıştır. Telefonun sağ üst tara

Upload Photo Butonu:
	Giriş yapan kullanıcı yeni Post atmasını sağlamaktadır.
	Basıldığında UploadActivity’ e gider





III. Resim Yükleme Ekranı(Upload Activity):

Burada giriş yapan kullanıcının resim yüklemesi buradan sağlanır. Resime üstüne basarak telefonun galerisine gider. Galeriden resim seçip tekrar Resim yükleme sayfasına gelir. Açıklama olarak maksimum 20 karakter sınırlı yazabilir.

Upload butonu
	Basıldığında Postu Firebase’e kaydeder. Daha sonra ana ekrana (Home) sayfasına gider.
	
Home
	Basıldığında Home ekranına geri döner


IV. Profil Sayfası:

Burada giriş yapılan kullanıcı kendi Kullanıcı adını ve profil fotorafını görebilir, düzenleyebilir.

Fotoğraf
	Basıldığında galeriye gider. Galeriden bir fotoğraf seçer ve profilActivity’e geri döner.

Edit Photo Butonu:
	Basıldığında galeriye gider. Galeriden bir fotoğraf seçer ve profilActivity’e geri döner.

Save Photo Butonu:
	Galeryden seçilen fotorafı giriş yapılan kullanıcın profil fotorafı olacak şekilde kaydeder.
	Daha sonra Ana ekrana geri döner.

V.Like ve Yorumlar:

A)Like
Burada Postun altındaki kalp butonuna basıldığında kırmızı olur ve Firebase’e kullanıcı bu postu beğendi olarak kayıt eder. Eğer tekrar basarda Firebase’deki kaydı siler.
B)Yorum

	Burada basıldığında RecyclerView karşımıza çıkar. Yorum yapanın solunda profil fotoğrafı ve kullanıcı adı bulunur. Sağ tarafında yaptığı yorum yer almaktadır


Yorum yapmak için Add a comment.. yazan yere yorum yazılır ve POST butonuna basılır. Daha sonra yaptığı yorum RecyclerView da gözükür ve FireBase’e kaydedilir.

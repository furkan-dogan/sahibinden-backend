# sahibinden.com

### Postman Dökümantasyonu

https://documenter.getpostman.com/view/29702154/2sA3XQhh63

### H2-Database üzerinden uygulamayı çalıştırmak

#### Gereksinimler

* Java 8 veya üzeri sürüm olmalıdır.

* Maven yüklü olmalıdır.

#### Projeyi Başlatmak

Projeyi başlatmak için aşağıdaki komutu kullanın:

`./mvnw spring-boot:run`

Eğer Maven wrapper (mvnw veya mvnw.cmd dosyası) kullanıyorsanız yukarıdaki komutu kullanabilirsiniz. Eğer Maven yüklü değilse, yerine mvn komutunu kullanabilirsiniz.

#### H2 Console'a Erişim

Uygulama başladıktan sonra, tarayıcınızdan http://localhost:8081/h2-console adresine giderek H2 veritabanı konsoluna erişebilirsiniz.

JDBC URL: jdbc:h2:mem:testdb

Username: sa

Password: (boş bırakın)

Bu bilgileri kullanarak H2 konsoluna bağlanabilir ve veritabanı üzerinde işlemler yapabilirsiniz. Postman kullanarak ise kolayca API endpoint'leri kullanabilirsiniz.

### Docker üzerinden uygulamayı çalıştırmak

Bu projeyi çalıştırmak için Docker Desktop veya Docker Engine'in yüklü olması gerekmektedir.

https://www.docker.com/get-started/

#### Docker Image Kurulumu

Terminal'den kaynak dosyası içerisindeyken;

`docker build -t dockerimage .`

Üstteki komutu yazmanız, projeyi Docker image'ına dönüştürür ve dockerimage adı altında etiketler.

`docker run -p 8081:8081 dockerimage`

Bir üstteki komut ise, dockerimage adlı Docker image'ından bir container başlatır ve host makinenizin 8081 portundan container'ın 8081 portuna yönlendirir.

### Checklist

[✓] **İlan Başlığı :** Harf (Türkçe karakterler dahil) veya Rakam ile başlamalıdır, en az 10, en fazla 50 karakter olabilir.
**Badwords.txt** dosyasında verilen kelimelerden herhangi biri girildiğinde ilan girişi engellenmelidir.

[✓] **İlan Detay Açıklaması :** En az 20, en fazla 200 karakter olabilir, özel karakterler kullanılabilir

[✓] **İlan Kategorisi :** Emlak, Vasıta, Alışveriş, Diğer olabilir (Yeni kategori eklenmeyecek gibi düşünülebilir)

### İlanın Yaşam Döngüsü ve Kuralları
[✓] İlan ilk verildiğinde Emlak, Vasıta ve Diğer kategorisi için "Onay Bekliyor" durumunda, bunların dışındaki kategoriler için ise "Aktif" durumda olmalıdır. Özetle Alışveriş kategorisi dışındakiler onaydan geçerek "Aktif" hale gelebilecektir.

[✓] Aynı kategoride, aynı başlık ve açıklamaya sahip ilan girildiğinde "Mükerrer" olarak işaretlenmelidir, mükerrer ilanların durumu güncellenemez.

[✓] "Onay Bekliyor" durumundaki ilan onaylandığında "Aktif" hale gelir. (İlanlar her daim onaylanacakmış ve reddedilmeyecekmiş gibi varsayılabilir)

[✓] Kullanıcı "Aktif" durumdaki veya "Onay Bekliyor" durumdaki ilanını "Deaktif" yapabilir.

#### API

[✓] İlan girişi

[✓] İlan aktivasyonu, deaktivasyon vb. durum değişiklikliği işlemleri

[✓] Kayıtlı tüm ilanların toplamda hangi durumlarda olduğunun istatistiksel -Aktif: 151, Deaktif: 71 gibi- olarak listelenmesi.
`GET /dashboard/classifieds/statistics`

[✓] Bir ilana ait zamanla oluşan tüm durum değişikliklerini listeleme (**BONUS**)

#### Testler
[✓] Unit Testler (Kapsam ve oranı geliştiricinin inisiyatifindedir)

#### Bonus
[✓] Swagger/Postman dokümanı hazırlanması

[ ] Çalışma süresi 5 milisaniyeden fazla süren isteklerin her defasında loglanması

[✓] Uygulamanın containerize edilmesi (Docker)

[✓] Bazı Entegrasyon Testleri yapıldı

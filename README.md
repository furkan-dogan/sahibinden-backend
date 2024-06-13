# sahibinden.com

### İlan API
Kullanıcıların ilan girebilecekleri ve ilanlarını diledikleri zaman yayınlayıp, diledikleri zaman yayından kaldırabilecekleri 
ve ilana ait birçok işlevi uygulayabilecekleri POC amaçlı yeni bir API projesi ekip olarak geliştirilmek isteniyor. 
POC Demo’sundaki yorumların ardından projelendirilip geliştirilmek arzu ediliyor, 
bu sebeple POC projesi üzerine bir şeyler inşaa edilebilecek seviyede olması da ekip içinde görüşülüyor. 
Bonus olarak belirtilmemiş maddelerin POC Demo'sunda sunulması düşünülmüyor, ekip güzel geri bildirimler almak için bir sürpriz de yapabilir.

POC kapsamında bir ilan için kullanıcıdan alınacak bilgiler ve ilana ait akışlar aşağıdaki gibidir.
 
* **İlan Başlığı :** Harf (Türkçe karakterler dahil) veya Rakam ile başlamalıdır, en az 10, en fazla 50 karakter olabilir.
 **Badwords.txt** dosyasında verilen kelimelerden herhangi biri girildiğinde ilan girişi engellenmelidir.
* **İlan Detay Açıklaması :** En az 20, en fazla 200 karakter olabilir, özel karakterler kullanılabilir
* **İlan Kategorisi :** Emlak, Vasıta, Alışveriş, Diğer olabilir (Yeni kategori eklenmeyecek gibi düşünülebilir)

### İlanın Yaşam Döngüsü ve Kuralları
* İlan ilk verildiğinde Emlak, Vasıta ve Diğer kategorisi için "Onay Bekliyor" durumunda, bunların dışındaki kategoriler için ise "Aktif" durumda olmalıdır. Özetle Alışveriş kategorisi dışındakiler onaydan geçerek "Aktif" hale gelebilecektir.
* Aynı kategoride, aynı başlık ve açıklamaya sahip ilan girildiğinde "Mükerrer" olarak işaretlenmelidir, mükerrer ilanların durumu güncellenemez.
* "Onay Bekliyor" durumundaki ilan onaylandığında "Aktif" hale gelir. (İlanlar her daim onaylanacakmış ve reddedilmeyecekmiş gibi varsayılabilir)
* Kullanıcı "Aktif" durumdaki veya "Onay Bekliyor" durumdaki ilanını "Deaktif" yapabilir.

### Geliştirmeler için Varsayımlar
* POC çalışması olduğundan ilan onaylayan ve ilan giren kullanıcılar aynı olabilir, auth mekanizması varmış gibi varsayılabilir.
* İlanın sadece durum bilgisinin güncelleneceği varsayılabilir. Başlık, detay, kategori bilgileri için bir güncelleme talebi bulunmamaktadır.
* "Deaktif" edilen bir ilanın tekrardan "Aktif" edilme senaryosunun POC Demo'sunda sunulmayacağı düşünülmüştür, bu kapsamda bir geliştirme beklentisi yoktur.

### API Kapsamı ve Beklentiler
#### API
İlgili kapsamda ekiple değerlendirip geliştirmeye karar verdiğiniz API kapsamları aşağıdaki gibidir.

* İlan girişi
* İlan aktivasyonu, deaktivasyon vb. durum değişiklikliği işlemleri
* Kayıtlı tüm ilanların toplamda hangi durumlarda olduğunun istatistiksel -Aktif: 151, Deaktif: 71 gibi- olarak listelenmesi.
`GET /dashboard/classifieds/statistics`
* Bir ilana ait zamanla oluşan tüm durum değişikliklerini listeleme (**BONUS**)

#### Testler
* Unit Testler (Kapsam ve oranı geliştiricinin inisiyatifindedir)

#### Bonus
* Swagger/Postman dokümanı hazırlanması
* Çalışma süresi 5 milisaniyeden fazla süren isteklerin her defasında loglanması
* Uygulamanın containerize edilmesi
* Enterasyon Testleri

### Proje Kısıtları
* Java 11
* Maven 3.6+
* Spring-Boot 2+
* Veritabanı olarak `in-memory` bir veritabanı kullanılmalıdır.
* API kurallarında `verilen endpoint dışında` tüm API tasarımı geliştiricinin inisiyatifindedir.
* Projenin süresi, repository davetinden sonra `beş` iş günüdür.
* Proje geliştirmeleri `main` branchinden alınan `feature/code-case` branchinde yapılıp `pull request` açılmalıdır.

### Öneriler
* Zamanı etkin kullanmak adına BONUS maddeleri sonraya bırakabilir veya pas geçebilirsin.
* Geliştirmeler sırasında OOP prensiblerine uyum, kodun modülerliğine dikkat edilmesi, API tasarımında standartları gözetilmesi fonksiyonel beklentilerin karşılanmasının yanında olumlu bir katkı sağlayacaktır.
* Proje için ihtiyaç duyduğun kütüphaneleri ekleme noktasında rahat olabilirsin.

### Sıkça Sorulan Sorular
* Repositorye push yapamıyorum.
    * Repository davetinde  read/write yetkin olacaktır. Profilindeki SSH Key tanımlarını kontrol edebilirsin. Problemin devam etmesi halinde repository bilgisini paylaşan kişi ile iletişime geçebilirsin.

_Her türlü bilgi için sahibinden.com IK Departmanı ile iletişime geçebilirsin._








/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coconut;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Taha Anıl Çolak ve Oğuzcan Biçer 
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private static Kurslar sepetim;
    private static Kurslar incelenecek;
    private static Kurslar goruntulenecek;
    private static ArrayList<Integer> places = numberAdder(10); // 1-10 arasi sayilar var icinden zar gibi random
    private static ArrayList<Integer> places2 = numberAdder(9);

    private static User theUser;
    private static List<Kurslar> kursList;
    private boolean isLogined;
    private final LoginDbController loginDbController = new LoginDbController();

    private static boolean isMessage;
    private String message;

    // user infos
    private String usr;
    private String mail;
    private String pw;
    private String pw2;

    private boolean okudumOnayladim;

    public LoginBean() throws SQLException {
        kursList = loginDbController.tumKurslar();

    }

    // MAIN METHODS
    public String login() throws SQLException {
        if (usr.equals("")) {
            message = "Kullanıcı adınızı giriniz.";
            return "login.xhtml";
        } else if (usr.length() < 5) {
            message = "Kullanıcı adınız çok kısa.";
            return "login.xhtml";
        } else if (usr.length() > 15) {
            message = "Kullanıcı adınız çok uzun.";
            return "login.xhtml";
        }
        if (pw.equals("")) {
            message = "Şifrenizi giriniz.";
            return "login.xhtml";
        } else if (pw.length() < 5) {
            message = "Şifreniz çok kısa.";
            return "login.xhtml";
        } else if (pw.length() > 15) {
            message = "Şifreniz çok uzun.";
            return "login.xhtml";
        }

        theUser = loginDbController.userGetir(usr, pw);
        if (theUser == null) {
            isLogined = false;
            message = "Geçersiz kullanıcı adı veya şifre.";

            return "login.xhtml";
        }
        message = " Tekrar hoş geldin, " + theUser.getUsername().substring(0, 1).toUpperCase() + "" + theUser.getUsername().substring(1) + ".";
        isLogined = true;
        return "index.xhtml";
    }

    public String register() throws SQLException {
        if (usr.equals("")) {
            message = "Kullanıcı adınız giriniz.";
            return "register.xhtml";
        } else if (usr.length() < 5) {
            message = "Kullanıcı adınız çok kısa.";
            return "register.xhtml";
        } else if (usr.length() > 15) {
            message = "Kullanıcı adınız çok uzun.";
            return "register.xhtml";
        }
        if (mail.equals("")) {
            message = "E-Postanızı giriniz.";
            return "register.xhtml";
        } else if (!mail.contains("@") || mail.contains("\"") || mail.contains(" ") || mail.contains("\\")) {
            message = "Geçersiz e-posta.";
            return "register.xhtml";
        }
        if (pw.equals("") || pw2.equals("")) {
            message = "Şifrenizi giriniz.";
            return "register.xhtml";
        } else if (pw.length() < 5) {
            message = "Şifreniz çok kısa.";
            return "register.xhtml";
        } else if (pw.length() > 15) {
            message = "Şifreniz çok uzun.";
            return "register.xhtml";
        } else if (!pw.equals(pw2)) {
            message = "Şifreler ayni olmalıdır.";
            return "register.xhtml";
        }
        if (!okudumOnayladim) {
            message = "Kullanıcı politikasını onaylayınız.";
            return "register.xhtml";
        }

        if (loginDbController.userRegister(usr, pw, mail)) {
            message = "Kayıt Başarılı.";

            return "index.xhtml";
        }
        message = "Kullanıcı adı veya mail kullanılmaktadır.";
        return "register.xhtml";
    }

    public String guncelle() throws SQLException {
        
        Customer customer = theUser.getCustomer();
        try {
            if(pw2.length()>0 || pw.length()>0) {
                
                if (pw.length()<5 && !pw.equals(theUser.getPassword())) {
                    message = "Eski sifre doğru değil!";
                    return "profileSettings.xhtml";
                }
                if(pw2.length()>4) {
                    theUser.setPassword(pw2);
                } else {
                    message = "Hatali yeni sifre!";
                    return "profileSettings.xhtml";
                }
                
            }
            
        } catch (NullPointerException ex) {

        }

        if (!customer.getMail().contains("@") || customer.getMail().contains("\"") || customer.getMail().contains(" ") || customer.getMail().contains("\\")) {
            message = "Geçersiz e-posta.";
            theUser = loginDbController.userGetir(theUser.getUsername(), theUser.getPassword());
            return "profileSettings.xhtml";
        }
        boolean isIt = loginDbController.customerGuncelle(theUser);
        if (!isIt) {
            message = "Girilen mail kullanimda!";
            return "profileSettings.xhtml";
        }
        if (theUser.getCustomer().getTelno().length() < 8) {
            message = "Hatali telefon numarasi";
            return "profileSettings.xhtml";
        }

        theUser = loginDbController.userGetir(theUser.getUsername(), theUser.getPassword());
        if (theUser.getCustomer() == null) {
            return "profileSettings.xhtml";
        }
        message = "Kullanıcı bilgileri başarıyla güncellendi.";
        return "index.xhtml";

    }

    public String siparisVer() throws SQLException {
        
        if(usr == null) {
            message = "İsim giriniz!";
            return "sepet.xhtml";
        } else if(usr.length()<5) {
            message = "Hatalı isim girdiniz! Tekrar deneyiniz.";
            return "sepet.xhtml";
        }
        
        if(mail == null) {
            message = "Kart numarasını giriniz!";
            return "sepet.xhtml";
        } else if(mail.length() != 19) {
            message = "Hatalı kart numarasını girdiniz! Tekrar deneyiniz.";
            return "sepet.xhtml";
        } else if(mail.charAt(4) != ' ' || mail.charAt(9) != ' ' || mail.charAt(14) != ' ') {
            message = "Hatalı kart numarasını girdiniz! Tekrar deneyiniz.";
            return "sepet.xhtml";
        }
        
        if(pw == null) {
            message = "Son kullanma tarihini giriniz!";
            return "sepet.xhtml";
        } else if(pw.length() != 5) {
            message = "Hatalı son kullanma tarihi girdiniz! Tekrar deneyiniz.";
            return "sepet.xhtml";
        } else if(pw.charAt(2) != '/') {
            message = "Hatalı son kullanma tarihi giriniz! Tekrar deneyiniz.";
            return "sepet.xhtml";
        }
        
        if(pw2 == null) {
            message = "CCV giriniz!";
            return "sepet.xhtml";
        } else if(pw2.length() != 3) {
            message = "Hatalı ccv girdiniz! Tekrar deneyiniz.";
            return "sepet.xhtml";
        } 
        
        
        if (theUser == null) {
            message = "İlk önce giriş yapınız!";
            return "login.xhtml";
        }
        
        
        
        
        loginDbController.kursSatinAl(theUser.getCustomer().getCustomerId(), sepetim.getKurs_id());
        theUser = loginDbController.userGetir(theUser.getUsername(), theUser.getPassword());
        sepetim = null;
        message = "Satın alma başarılı!";
        return "index.xhtml";
    }

    //##########################################################################
    // Buttonlar
    public String logout() {
        theUser = null;
        return "index?faces-redirect=true";
    }

    public String hesapSil() throws SQLException {
        loginDbController.hesabiSiler(theUser.getCustomer().getCustomerId());
        theUser = null;
        message = "Hesabınız başarıyla silinmiştir.";
        return "index.xhtml";
    }

    public String sepetiBosalt() {
        sepetim = null;
        return "sepet.xhtml";
    }

    //##########################################################################
    // View
    // ANA SAYFA VIEW LERI index.xhtml
    public String showMeSayfaBaslik(String mnumber) {
        int number = Integer.parseInt(mnumber);
        return kursList.get(places.get(number)).getBaslik();
    }

    public String showMeSayfakAciklama(String mnumber) {
        int number = Integer.parseInt(mnumber);
        return kursList.get(places.get(number)).getkAciklama();
    }

    // 1 2 3 4 5 6 7 8 9 10
    public String showMeSayfaPicture(String mnumber) {
        int number = Integer.parseInt(mnumber);
        int temp = kursList.get(places.get(number)).getKurs_id();
        return "resources/images/product-" + temp + ".jpg";
    }

    public String showMeYazarAd(String mnumber) {
        int number = Integer.parseInt(mnumber);
        return kursList.get(places.get(number)).getYazar().getAdSoyad();
    }

    public String showMeYazarJob(String mnumber) {
        int number = Integer.parseInt(mnumber);
        return kursList.get(places.get(number)).getYazar().getJob();
    }

    public String showMeYazarBio(String mnumber) {
        int number = Integer.parseInt(mnumber);
        return kursList.get(places.get(number)).getYazar().getBio();
    }

    public String showMeYazarPicture(String mnumber) {
        int number = Integer.parseInt(mnumber);
        int temp = kursList.get(places.get(number)).getYazar().getYazar_id();

        return "resources/images/" + temp + ".jpg";
    }

    //##########################################################################
    // SEPET.xhtml VIEW LERI
    public String showMeBaslik() {
        if (sepetim == null) {
            return "bos";
        }
        return sepetim.getBaslik();
    }

    // 1 2 3 4 5 6 7 8 9 10
    public String showMePicture() {
        if (sepetim == null) {
            return "#";
        }
        int temp = sepetim.getKurs_id();
        return "resources/images/product-" + temp + ".jpg";
    }

    public String getPrice() {
        if (sepetim != null) {
            return "₺ 39.99";
        }
        return "₺ 0";
    }

    // #########################################################################
    // product.xhtml
    public String showMeBaslik2() {
        return incelenecek.getBaslik();
    }

    public String yazarResmi() {
        int temp = incelenecek.getYazar().getYazar_id();
        return "resources/images/" + temp + ".jpg";
    }

    public String aciklamaBir() {
        int temp = incelenecek.getKurs_id();
        switch (temp) {
            case 1:
                return "JavaScript, yaygın olarak web tarayıcılarında kullanılmakta olan dinamik bir programlama dilidir. JavaScript ile yazılan istemci tarafı betikler sayesinde tarayıcının kullanıcıyla etkileşimde bulunması, tarayıcının kontrol edilmesi, asenkron bir şekilde sunucu ile iletişime geçilmesi ve web sayfası içeriğinin değiştirilmesi gibi işlevler sağlanır. JavaScript, Node.js gibi platformlar sayesinde sunucu tarafında da yaygın olarak kullanılmaktadır.Bu kurs Javascript'i A'dan Z'ye öğretecek şekilde tasarlanmıştır. Javascript'i kullanarak profesyonel dinamik web siteleri geliştirmenin inceliklerini öğreneceksiniz. ";
            case 2:
                return "Hiper Metin İşaretleme Dili (İngilizce Hypertext Markup Language, ks. HTML) web sayfalarını oluşturmak için kullanılan standart metin işaretleme dilidir. Dilin son sürümü HTML5'tir.\nHTML, bir programlama dili olarak tanımlanamaz. Zira HTML kodlarıyla kendi başına çalışan bir program yazılamaz. Ancak bu dili yorumlayabilen programlar aracılığıyla çalışabilen programlar yazılabilir. Programlama dili denilememesinin nedeni tam olarak budur. Temel gereği yazı, görüntü, video gibi değişik verileri ve bunları içeren sayfaları birbirine basitçe bağlamak, buna ek olarak söz konusu sayfaların web tarayıcısı yazılımları tarafından düzgün olarak görüntülenmesi için gerekli kuralları belirlemektir. HTML kodunu web tarayıcıları okur, yorumlar ve görsel hale dönüştürürler, dolayısıyla aynı HTML kodunun farklı tarayıcılarda farklı sonuç vermesi olasıdır.";
            case 3:
                return "AT&T Bell laboratuvarlarında, Ken Thompson ve Dennis Ritchie tarafından UNIX İşletim Sistemi' ni geliştirebilmek amacıyla B dilinden türetilmiş yapısal bir programlama dilidir. Geliştirilme tarihi 1972 olmasına rağmen yayılıp yaygınlaşması Brian Kernighan ve Dennis M. Ritchie tarafından yayımlanan \"C Programlama Dili\" kitabından sonra hızlanmıştır. Günümüzde neredeyse tüm işletim sistemlerinin yapımında %95' lere varan oranda kullanılmış, hâlen daha sistem, sürücü yazılımı, işletim sistemi modülleri ve hız gereken her yerde kullanılan oldukça yaygın ve sınırları belirsiz oldukça keskin bir dildir.";
            case 4:
                return "Java, Sun Microsystems tarafından üretilen ve yazılım uygulamaları geliştirmeye yardımcı yazılımlar bütünüdür. Java'nın kullanım alanı gömülü aygıtlardan cep telefonlarına, kurumsal sunuculardan süper bilgisayarlara uzanmaktadır. Cep telefonları, Web sunucuları ve kurumsal uygulamalarda sıkça kullanılan Java'nın masaüstü bilgisayarlardaki kullanımı daha az yaygındır. Ne var ki, bu ortamda çalışan Java uygulamacıkları Dünya Çapında Ağ üzerinde gerçekleştirilen işlemlerde kullanım kolaylığı sağlamaktadır. Çalıştırılmadan önce Java bitkoduna dönüştürülecek kaynak kodu çoğunlukla Java programlama dilinde geliştirilmektedir.";
            case 5:
                return "Adobe Photoshop, Adobe Inc.'nin Windows ve macOS için geliştirip sunduğu piksel tabanlı görüntü, resim ve fotoğraf düzenlemede sayısal fotoğraf işleme yazılımıdır. Vektörel işlemlerde ve yazı işleme konusunda da bazı yetenekleri olmakla beraber, pazar lideri olmasını sağlayan özelliği bit resim işleme işlevini de taşıyan Photoshop; kuşkusuz bilgisayar dünyasının en kuvvetli yazılımlarındandır.O zamandan beri, yazılım sadece raster grafik düzenlemede değil, bir bütün olarak dijital sanatta endüstri standardı haline geldi.  Photoshop; raster görüntülerini birden çok katmanda düzenleyebilir ve maskeleri, alfa kompozisyonlarını ve RGB, CMYK, CIELAB, spot renk ve çift ton gibi çeşitli renk modellerini destekler.";
            case 6:
                return "SQL, (İngilizce \"Structured Query Language\", Türkçe: Yapılandırılmış Sorgu Dili, telaffuz: ɛs kjuː ˈɛl/) verileri yönetmek ve tasarlamak için kullanılan bir dildir. SQL, kendisi bir programlama dili olmamasına rağmen birçok kişi tarafından programlama dili olarak bilinir. SQL herhangi bir veri tabanı ortamında kullanılan bir alt dildir. SQL ile yalnızca veri tabanı üzerinde işlem yapılabilir; veritabanlarında bulunan sistemlere bilgi ekleme, bilgi değiştirme, bilgi çıkarma ve bilgi sorgulama için kullanılmaktadır. Özellikle de ilişkisel veritabanı sistemleri üzerinde yoğun olarak kullanılmaktadır. SQL'e özgü cümleler kullanarak veri tabanına kayıt eklenebilir, olan kayıtlar değiştirilebilir, silinebilir ve bu kayıtlardan listeler oluşturulabilir. ";
            case 7:
                return "Twitter Bootstrap (ya da kısaca Bootstrap) açık kaynak kodlu, web sayfaları veya uygulamaları geliştirmek için kullanılabilecek araçlar bütünü ve önyüz çatısı. Bootstrap, web sayfaları veya uygulamalarında kullanılabilecek, HTML ve CSS tabanlı tasarım şablonlarını içerir. Bu şablonlar form, navigasyon çubuğu, buton gibi arayüz bileşenleri oluşturmakta kullanılabilmektedir. Ocak 2021 itibarı ile Bootstrap, Github üzerinde 148 binin üzerinde \"star\" ile 71 binin üzerinde \"fork\" sayılarına ulaşarak, sitenin en popüler projelerinden biri olmuştur.";
            case 8:
                return "Nasıl okunması gerektiği konusunda bir genel uzlaşma olmamakla birlikte çoğunlukla yazıldığı gibi ajaks olarak okunurken, kimileri tarafından aynı yazımlı ismiyle futbol takımı örnek gösterilerek ayaks olarak okunmaktadır. İngilizcede ey-ceks olarak okunur. En yaygın kullanım alanı, sayfayı yeniden yüklemeye gerek kalmaksızın, sayfada görünür değişiklikler yapmaktır. XMLHttpRequest kullanılarak birden fazla bağımsız işlem yapılabilir. Bazı bilişim uzmanları, AJAX'ın HTML ve XML'den sonra en yenilikçi İnternet yazılımı olduğunu ve Web 2.0.'ı sonlandırıp, 3. evrenin kapısını açtığını öne sürmüşlerdir. Asynchronous JavaScript and XML sözcüklerinin kısaltması olan Ajax, etkileşimli (interaktif) web uygulamaları yaratmak için kullanılan bir web programlama tekniğidir. Temel amacı arka planda sunucuyla ufak miktarda veri değişimi sayesinde sayfayı daha hızlı güncelleyebilen web sayfaları yapmak, dolayısıyla kullanıcının istediği her anda bütün web sayfasını güncellemek derdinden kurtulmaktır. Bu da web sayfasının etkileşimini, hızını ve kullanılabilirliğini artırmak demektir. ";
            case 9:
                return "Ağ güvenliği, çok sayıda teknoloji, cihaz ve süreci kapsayan geniş kapsamlı bir terimdir. En basit haliyle, yazılım ve donanım teknolojilerinden faydalanarak bilgisayar ağlarının ve verilerin bütünlük, gizlilik ve erişilebilirliğini korumak için tasarlanmış bir kurallar ve yapılandırmalar dizisidir. Boyutu, sektörü ve altyapısı ne olursa olsun, her kurum günümüzde sürekli artan siber tehditlerden korunabilmek için bir dereceye kadar ağ güvenliği çözümlerine ihtiyaç duymaktadır. Günümüzün ağ mimarisi karmaşıktır ve sürekli değişmekte olan bir tehdit ortamıyla karşı karşıyadır; saldırganlar da sürekli olarak güvenlik açıklarını bulmaya ve suistimal etmeye çalışmaktadır. Bu güvenlik açıkları, cihazlar, veriler, uygulamalar, kullanıcılar ve tesisler dahil olmak üzere pek çok farklı alanda mevcut olabilir. Dolayısıyla, günümüzde tehditleri, suistimalleri ve yasalara uyumsuzluk durumlarını hedef alan pek çok ağ güvenliği yönetimi araç ve uygulamaları mevcuttur. Yalnızca birkaç dakikalık bir arıza süresinin, bir işletmenin kârı ve itibarı üzerinde geniş çaplı bir tahribat yaratabileceği düşünüldüğünde, bu koruma önlemlerine sahip olunması hayati önem taşımaktadır.";
            case 10:
                return "Bilgisayar bilimi, bilgisayarların tasarımı ve kullanımı için temel oluşturan teori, deney ve mühendislik çalışmasıdır. Hesaplamaya ve uygulamalarına bilimsel ve pratik bir yaklaşımdır. Bilgisayar bilimi; edinim, temsil, işleme, depolama, iletişim ve erişimin altında yatan yönteme dayalı prosedürlerin veya algoritmaların fizibilitesi, yapısı, ifadesi ve mekanizasyonunun sistematik çalışmasıdır. Bilgisayar biliminin alternatif, daha özlü tanımı \"büyük, orta veya küçük ölçekli algoritmik işlemleri otomatikleştirme çalışması\" olarak nitelendirilebilir. Bir bilgisayar bilimcisi, hesaplama teorisi ve hesaplama sistemlerinin tasarımı konusunda uzmanlaşmıştır. Alanları teorik ve pratik disiplinlere ayrılabilir. Bilgisayar grafikleri gibi alanlar, gerçek dünya görsel uygulamalarını vurgularken, hesaplamalı karmaşıklık teorisi (hesaplama ve zor olan sorunların temel özelliklerini araştıran) gibi bazı alanlar oldukça özeldir.";
            default:
                return "placeholder";
        }
    }

    public String aciklamaIki() {
        int temp = incelenecek.getKurs_id();
        switch (temp) {
            case 1:
                return "JS öğrendiniz, mobil veya web uygulamalarınız için bir backende ihityaçınız var. Nodejs kullanarak kendi backend çözümünüzü üretebilir, Firebase kullanmaktan kurtulabilirsiniz. Nodejs ile ilgili kapsamlı dersleri izleyip öğrendikten sonra, API servisi olusturabilir, yönetim panelli web siteleri yapabilirsiniz. Ve bunları yaparken her hangi bir programlama geçmişine sahip olmanız gerekmiyor.Kursu daha önceden hiç bir programlama temeliniz olmadığını varsayarak hazırladım. Tüm kursu dikkatle izleyen biri olursanız html css konularına hakim, js dilini detaylarıyla bilen ve de nodejs ile kendi backend çözümünü üreten biri olmanız çok kolay. Kurs, 77+ saat içerik ve de içerdiği konulardan dolayı udemydeki en kapsamlı JS ve Nodejs kurslarından biridir.";
            case 2:
                return "Web sitelerinin temelini HTML oluşturur. Tüm web sitelerinin (microsoft.com, facebook.com, vb.) arayüzü hep HTML ile oluşturulmuştur. HTML  kursumuzda daha çok HTML in temel yapısını oluşturan etiketler üzerinde duracağız. Fazla detaylara girilmeden, örnekler ile kolay ve pratik olarak HTML i hep beraber öğreneceğiz. Bu kursun ana amacı kendi web sitelerinizi oluşturacak altyapıyı sizlere sunmaktır. Bu kursu bitirdikten sonra: HTML etiketlerini tanıyor olacaksınız. Bir web sayfasının nasıl oluşturulduğunu öğrenmiş olacaksınız. Statik ve dinamik web sayfaları arasındaki farkı anlamış olacaksınız.";
            case 3:
                return "Bu kurs iş hayatına yönelik bir kurs olup.Sıfırdan başlayıp C'nin neredeyse tüm araçları anlatıldığı profesyonel bir kurstur.Bu kurstan bir üniversite ögrencisi de rahatlıkla faydalanabilir.Kursu özenle takip ederse okul derslerindeki başarısını rahatlıkla artırabilir. Kursu iş hayatına hazırlananan veya iş hayatında proje geliştiren yazılım geliştirciler için referans kaynak niteliğindedir. Kursu hiç programlama bilgisi olmayan biride çok rahat kullanıp faydalanabilir... Kursu okul dersine çalışmak isteyen öğrencilerde rahatlıkla faydalanabilir. Kursta çok sayıda örneklere ve ayrıntılara yer verilmiştir.Klasik sorular dışında farklı sorular ve mülakat sorularınada yer verilmiştir. Kursun soru cevap bölümüde açık olup.Kursla ilgili anlaşılmayan her noktaya soru cevap bölümünden soru sorabilirsiniz.Kursta şuana kadar yanıtlanmamış soru yoktur. Kurs dışında bir yazılımcı ve bir mühendis kendini nasıl geliştitrebilir neler öğrenmesi gerekir dileyen ögrencilere destek verilmektedir. 10 yıllık C Programcılarıda muhakak kendileri için yararlı bilgiler bulacaktır. Kurs uzun bir çalışmanın ürünü olup her video özenle çekilmiştir.";
            case 4:
                return "Java ile Nesne Yönelimli Programlamayı öğrenerek uzmanlaşmanın ve profesyonel uygulamalar geliştirmenin tadını çıkarın. Bu kursta sıfırdan Java ile Nesne Yönelimli Programlamanın tüm konseptlerini öğrenerek Javada hep beraber uzmanlaşmaya çalışacağız ve kurs bitiminde profesyonel Java uygulamaları çıkarabilecek seviyeye geleceğiz. Kurs boyunca : Tüm bölümlerde temel konularımızı Netbeans IDE ortamında geliştireceğiz. Aynı zamanda öğrendiklerimizi daha kalıcı kılmak için Smart Notebook ve Grafik Tablet kullanarak şekil üzerinden de konseptleri anlamaya çalışacağız. Temel konular anlatılırken özellikle iş mülakatlarında oldukça fazla sorulan konseptleri özellikle vurgulamaya çalışacağız. Tüm bölümlerde öğrendiğimiz her yeni konseptten sonra mini projeler ve uygulamalar gerçekleştireceğiz. Kursun son bölümlerinde öğrendiğimiz tüm bilgileri kullandıktan sonra büyük projeler geliştirmeye çalışacağız.";
            case 5:
                return "16 yıllık piyasa tecrübesini bu eğitim ile aktarma zamanı geldi. Bu eğitimde Photoshop CC versiyonu ile harika çalışmaları yapmayı temelden başlayıp öğrenecek, aslında ne kadar kolay olduğunu göreceksiniz. Detaylı ve başa dönüşlere ihtiyaç duymadan son videoya kadar programa hakim olmuş olacaksınız. Günlük hayatımızda hemen hemen hergün karşılaştığımız photoshopla içiçe olmak aslında zorunluluk haline gelmiştir. Örneğin bir sunum yaparken bile veya bir youtube kapak hazırlarken photoshop olmazsa olmazlardandır. Tabi ki bununla da sınırlamak istemedim. Photoshopla harika bir şekilde entegreli çalışacağımız Adobe Bridge ve  Adobe Lightroom'u da (Özellikle fotoğrafçılar için) öğrenmiş olacaksınız. Yani 3 eğitim bir arada! Temelden ileri seviyeye doğru anlatımı yapılmış, uygulamalarla pekiştirilmesi sağlanmıştır. Çalışmaların baskıya gönderilme aşaması aslında kendi başına bir eğitim ve tecrübedir. Bunun eksikliğini yaşamamanız için de dikkat edilmesi gereken hususlar da ele alınarak, bu aşamada eksiksiz bir eğitim verilmesi amaçlanmıştır.";
            case 6:
                return "Bildiğiniz üzere SQL ve Microsoft SQL Server üzerine bir çok eğitim hazırlıyorum ve bu eğitimler sizlerden büyük ilgi görüyor. Bunun en büyük sebebi şüphesiz sizlerle doğru frekansı yakalamak adına yapmaya çalıştıklarım. Kursların iyi anlaşılması adına sürekli gerçek senaryolar, gerçek veriler ve gerçek hayat hikayeleri üzerinden dersler hazırlamaya çalışıyorum. Bu noktada sanırım bir miktar başarılı olduğumu düşünüyorum. Zira SQL gibi spesifik bir konuda 100 ülke ve 25.000+ öğrenciye ulaşmış durumdayız. Bir çok noktada kursları daha iyileştirme noktasında neler yapabileceğimize kafa yoruyoruz ve bu noktada sizlerden gelen geri dönüşleri de dikkate alarak en çok talebin daha fazla Pratik, ödev ve soru cevap konularında geldiğini gözlemledik. Bu konuya ben de çok katılıyorum. Bir konuyu en iyi öğrenmenin yolu o konuda Pratik yapma ile doğrudan orantılı ve belki de bu alıştırma eğitimleri eğitimin kendisinden daha önemli ve daha zengin içeriğe sahip olmalı diye düşünüyorum.";
            case 7:
                return "Unutma! “Kimse senin dalgalarla nasıl boğuştuğuna bakmaz. Gemiyi limana getirip getirmediğine bakar.” Ben kaptanınız Emrah Yüksel olarak bu eğitim serüvenin sonunda gerçekten sana vaad ettiğim ve hayal kurmanı sağladığım her şeyi yapabilmeni sana taahhüt ediyor sadece kursa kayıt oluncaya kadar değil kurs öğrenme süresi boyuncada sana destek olacağım sözünü veriyorum. Bu Kursta Neler Yapacağız? Bu kurs tam anlamıyla sizi Bootstrap 3 veya Bootstrap 4 hakkında hiçbir şey bilmeseniz de sıfırdan harika web tasarımları/temalar yapabilmeniz için Bootstrap 4 'ün kullanımını öğretmektedir. Bu kursa başlamak için Bootstrap'ın önceki sürümleri hakkında bilgi sahibi olmanıza gerek yok. Kurs içerisinde Bootstrap 4 için temel CSS bilgisi de sunulduğundan sadece temel bilgisayar kullanım bilginiz olması yeterli";
            case 8:
                return "JavaScript artık web sayfalarının vazgeçilmezi oldu, jquery gibi bir çok kütüphane, mobil responsive tasarımlar, ajax işlemleri, json ile verileri yorumlama gibi ana işlemler hep javascript ile yapılıyor. Bu eğitimde Ajax ve Json ile güzel bir başlangıç yapıp, temel konulardan ileri konulara JavaScripti öğreneceğiz. JavaScript alt yapısı olan kişiler rahatlıkla Node.js , React.js gibi konulara geçiş yapabilirler. Eskiden JavaScript sadece bir kaç satır gerekli kodu modifiye etmek için lazımdı ama artık sadece düzenleme değil, bu kodlarıda yazmanız gerekecek. Gelecekte JavaScript iyi bilmeyen bir web master kabul edilmeyecektir. İşte bu sebeple JavaScript Öğrenmek için Doğru Zamandasınız.";
            case 9:
                return "Sizlere tecrübemizle, sektördeki en geçerli bilgileri, en uygun sırada, en uygun sürede  sunan yoğunlaştırılmış  bir içerik hazırladık.  Konunun  özüne odaklanarak hızla ilerlemenizi hedefledik. Kariyerinizde beyaz şapkalı hacker veya sızma testi uzmanı olma hedefleriniz varsa, ağ ve web uygulama güvenliği konularında detaylı ve uygulamalı bilgiler edinmek istiyorsanız, bilgisayar ve ağ bilginizi daha ileri bir noktaya taşımak istiyorsanız bu Uygulamalı Beyaz Şapkalı Hacker Kursu'nu çok seveceksiniz. Boğaziçi Üniversitesi Siber Güvenlik Merkezi Topluluğu tarafından titizlikle hazırlanan bu kursta sektörde geçerliliği olan teknik bilgiye erişimin yanı sıra, sızma testleri ve etik hacker olmakla ilgili her sorunuza alanında uzman eğitmenlerden yanıt alabileceksiniz. En temel seviyeden başlayarak temel linux, ağ ve sistem bilgisi, Kali Linux ve VirtualBox kullanımı, OSI katmanları, ağ ve servis protokolleri, Nmap, NetBIOS, Netdiscover araçları ile aktif bilgi toplama konularının yanı sıra; orta ve ileri seviye zafiyet sömürme araçları, metasploit, parola saldırıları, hash ve kripto kavramları, ağ saldırıları, bilinen zafiyetler, DDos saldırıları, ARP Saldırıları, kablosuz ağ saldırıları ile devam edeceğimiz kursun web uygulama güvenliği bölümünde HTTP metot ve mesajları, Brute-force saldırıları, sunucuda komut çalıştırma, CSRF, XSS, LFI ve SQL Injection zafiyetlerinin tamamını uygulamalı olarak zafiyetlerin arka planlarını inceleyerek öğrenebileceksiniz. ";
            case 10:
                return "Temel bilgisayar bilgilerini öğreneceksiniz. Bilgisayar kavramından başlayan kursumuz bilgisayar çeşitleri, tarihçesi ile devam edecektir. Donanım ve yazılım kavramlarını öğreneceğiniz bu eğitim setinde donanım birimlerini yakından tanıyacaksınız.  İç-Dış donanım birimleri olarak kategorize edilen donanım parçaları ayrıca giriş ve çıkış birimleri olarak detaylandırılmıştır.  Yazılım kavramının çeşitlerini de bu eğitim sayesinde göreceksiniz. Temel düzey için planlanmış bu eğitim ile ayrıca klavye tuşlarını tanıyacak her tuşun görevini öğreneceksiniz. Klasör oluşturma, silme, yeniden adlandırma gibi temel düzey işlemleri yapabileceksiniz. Denetim masasında yer alan ağ ve paylaşım merkezi, bölge ve dil, dosya gezgini seçenekleri gibi öğeleri kullanarak bilgisayar ayarlarınızı değiştirebileceksiniz. Tek başınıza bilgisayarınıza format atabileceksiniz.";
            default:
                return "placeholder";
        }
    }

    // #########################################################################
    // my-courses.xhtml
    public String showMeMyCoursesBaslik(String mnumber) {
        int number = Integer.parseInt(mnumber) - 1;
        try {
            return theUser.getCustomer().getSatinAlinanKurslar().get(number).getBaslik();
        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            return "placeholder";
        }
    }

    public String showMeMyCourseskAciklama(String mnumber) {
        int number = Integer.parseInt(mnumber) - 1;
        try {
            return theUser.getCustomer().getSatinAlinanKurslar().get(number).getkAciklama();
        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            return "placeholder";
        }
    }

    // 1 2 3 4 5 6 7 8 9 10
    public String showMyCoursesPicture(String mnumber) {
        int number = Integer.parseInt(mnumber) - 1;
        try {
            int temp = theUser.getCustomer().getSatinAlinanKurslar().get(number).getKurs_id();
            return "resources/images/product-" + temp + ".jpg";
        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            return "#";
        }
    }
    public ResultSet showTablo() throws SQLException{
        return loginDbController.ayinBirincisi();
    }
    
    
    
    
    
    // sub methods
    public String setGoruntulenecek(String mnumber) {
        int number = Integer.parseInt(mnumber) - 1;
        goruntulenecek = theUser.getCustomer().getSatinAlinanKurslar().get(number);

        return "product-purchased.xhtml";
    }

    public boolean isFullSepet() {
        if (sepetim != null) {
            return true;
        }
        return false;
    }

    public void shuffleMenu() {
        Collections.shuffle(places);
    }

    public String addSepet(String mnumber) {
        
        int number = Integer.parseInt(mnumber);
        sepetim = kursList.get(places.get(number));

        return "sepet.xhtml";
    }

    public String addSepet2() {
        sepetim = incelenecek;

        return "sepet.xhtml";
    }

    public String incele(String mnumber) throws SQLException {
        int number = Integer.parseInt(mnumber);
        incelenecek = kursList.get(places.get(number));

        return "product.xhtml";
    }

    private static ArrayList numberAdder(int num) {
        ArrayList<Integer> temp = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            temp.add(i);
        }
        return temp;
    }

    // my-courses.xhtml
    public String isThisShowable(String mnumber) {
        int number = Integer.parseInt(mnumber);
        try {
            if (number <= theUser.getCustomer().getSatinAlinanKurslar().size()) {
                return "col-md-4";
            }

        } catch (NullPointerException ex) {
            return "col-md-4 d-none";
        }
        return "col-md-4 d-none";
    }

    public String isThisNotShowable(String mnumber) {
        int number = Integer.parseInt(mnumber);
        try {
            int id = kursList.get(places.get(number)).getKurs_id();

            for (int i = 0; i < theUser.getCustomer().getSatinAlinanKurslar().size(); i++) {
                if (id == theUser.getCustomer().getSatinAlinanKurslar().get(i).getKurs_id()) {
                    return "col-md-4 d-none";
                }
            }

        } catch (NullPointerException ex) {
            return "col-md-4";
        }
        return "col-md-4";
    }

    
    
    // GETTER SETTER
    public String getLogined() {
        if(theUser != null) {
            return "nav-item pe-4";
        }
        return "d-none";
    }
    public String getNotLogined() {
        if(theUser == null) {
            return "nav-item pe-4";
        }
        return "d-none";
    }

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPw2() {
        return pw2;
    }

    public void setPw2(String pw2) {
        this.pw2 = pw2;
    }

    public Kurslar getSepetim() {
        return sepetim;
    }

    public void setSepetim(Kurslar sepetim) {
        this.sepetim = sepetim;
    }

    public static Kurslar getIncelenecek() {
        return incelenecek;
    }

    public static User getTheUser() {
        return theUser;
    }

    public static Kurslar getGoruntulenecek() {
        return goruntulenecek;
    }

    public boolean isOkudumOnayladim() {
        return okudumOnayladim;
    }

    public void setOkudumOnayladim(boolean okudumOnayladim) {
        this.okudumOnayladim = okudumOnayladim;
    }

    // silinecek
    public int toplamKurs() {
        if (theUser == null) {
            return 0;
        }
        return theUser.getCustomer().getSatinAlinanKurslar().size();
    }

    public String getUsername() {
        if (theUser == null) {
            return "placeholder";
        }
        return theUser.getUsername();
    }

    public String getPassword() {
        return theUser.getPassword();
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getCustomer_id() {
        return theUser.getCustomer().getCustomerId();
    }

    public String isFailMessage() {
        isMessage = message != null;

        if (isMessage) {
            return "alert alert-danger alert-dismissible fade show d-block";
        }
        return "d-none";
    }

    public String isSuccessMessage() {
        isMessage = message != null;

        if (isMessage) {
            return "alert alert-success alert-dismissible fade show d-block";
        }
        return "d-none";
    }

    public String getMessage() {
        try {
            return message;
        } catch (NullPointerException ex) {
            return "";
        }
    }

}

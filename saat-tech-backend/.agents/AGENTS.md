# Rules`n`n- **Do not modify the user's code directly.** The user prefers to make changes themselves. Provide clear instructions, file paths, and code snippets for the user to copy and paste.

- Koda asla müdahale etme, dosyaları değiştirme, sadece bana kopyalamam için kod bloklarını ver ayrıca verdiğin kodda nereleri değiştirdiğini ve neden yaptığını belirt.

- Yazdığın kodlarda (değişken adları, mesajlar, exceptionlar vb.) yorum satırları haricinde hiçbir yerde Türkçe kullanma, her zaman İngilizce kullan.

- **Her zaman kurumsal (enterprise) seviyede, yüksek güvenlikli ve ölçeklenebilir çözümler sun.** Projenin küçük çaplı veya yerel (local) ortamda çalıştığını varsayarak asla mimari kısayollara (shortcut) başvurma veya geçici çözümler (quick-and-dirty) önerme. Her zaman devasa ve canlı bir üretim (production) ortamında kod yazıyormuş gibi hareket et ve en iyi endüstri standartlarını uygula.

- Spring Boot projelerinde `application.properties` üzerinden veri okurken asla `@Value` anotasyonunu kullanma. Her zaman `@ConfigurationProperties` kullanarak kurumsal mimariye uygun config sınıfları oluştur ve verileri bu sınıflar üzerinden çek.

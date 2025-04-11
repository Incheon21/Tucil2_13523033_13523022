# Tugas Kecil IF2211 Strategi Algoritma	Kompresi Gambar dengan Quadtree

![Ilustrasi Quadtree](./test/output/rot.gif)

## Deskripsi Program

Program ini merupakan implementasi algoritma kompresi gambar menggunakan struktur data Quadtree dengan pendekatan Divide and Conquer. Algoritma secara rekursif membagi gambar menjadi empat kuadran hingga mencapai ambang batas homogenitas tertentu atau ukuran blok minimum tercapai. Setiap region yang homogen direpresentasikan oleh warna rata-ratanya untuk menghasilkan gambar terkompresi.

## Fitur

- **Beberapa Metode Pengukuran Error:**
  - Variance
  - Mean Absolute Deviation (MAD)
  - Max Pixel Difference
  - Entropy
  - Structural Similarity Index (SSIM)

- **Opsi Kompresi:**
  - Set target tingkat kompresi (program akan otomatis mencari parameter optimal)
  - Set threshold dan ukuran blok minimum secara manual

- **Visualisasi:**
  - Menghasilkan GIF yang menunjukkan proses kompresi pada setiap level kedalaman tree

## Cara Menjalankan

1. Kompilasi program:
   ```
   javac -d bin src/*.java
   ```

2. Jalankan program:
   ```
   java -cp bin Main
   ```

3. Ikuti petunjuk untuk:
   - Memasukkan path gambar input
   - Memilih metode kompresi (target rate atau parameter manual)
   - Menentukan path output untuk gambar terkompresi dan GIF (opsional)

## Struktur Program

- **QuadTree.java**: Implementasi utama dari struktur quadtree
- **QuadTreeNode.java**: Kelas node untuk quadtree
- **Method.java**: Berbagai metode pengukuran error
- **FileProcessor.java**: Utilitas penanganan file
- **GifSequenceWriter.java**: Utilitas pembuatan GIF
- **Main.java**: Antarmuka pengguna dan orkestrasi program

## Algoritma

1. **Divide:** Secara rekursif membagi gambar menjadi empat kuadran
2. **Conquer:** Untuk setiap region, hitung error menggunakan metode yang dipilih

## Contoh Penggunaan

Program akan meminta:
1. Path gambar input (gambar asli untuk dikompresi)
2. Target tingkat kompresi (0-1) atau 0 untuk mengatur parameter secara manual
   - Jika manual: pilih metode error, threshold, dan ukuran blok minimum
3. Path gambar output
4. Apakah ingin membuat visualisasi GIF
   - Jika ya: path untuk output GIF

## Hasil

Setelah kompresi, program menampilkan:
- Waktu eksekusi
- Ukuran gambar asli dan terkompresi
- Persentase kompresi
- Kedalaman tree dan jumlah node
- Path output

## Author
1. 13523022 Kenneth Ricardo Chandra
2. 13523033 Alvin Christopher Santausa
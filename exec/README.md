# ๐ป With SSAFY ๐ป

 ๐ฉ ๋ชจ๋  ์ธํผ์ธ๋ค์ ํธ๋ฆฌํ ์ฌ์ธ์ํจํค์ง ์๋น์ค, ๋ค์ํ ์ต๋ช ์ปค๋ฎค๋ํฐ์ ํ๋ฒ์ ํ์ธํ  ์ ์๋ ์ปค๋ฆฌํ๋ผ ๋ฐ ์คํฐ๋์ ํ๋น๋ฉ๊น์ง ๋ค๋ฅธ ํ๋ซํผ์ ์ฌ์ฉํ์ง ์๊ณ  ํธ๋ฆฌํ๊ฒ ์๋น์ค๋ฅผ ์ด์ฉํ  ์ ์๋ ์ ํ๋ฆฌ์ผ์ด์

๐ [์๋์ธํผ ์ฌ์ดํธ](https://k6d201.p.ssafy.io/)

## ๋น๋ ๋ฐ ๋ฐฐํฌ

### ๐ ๊ฐ๋ฐํ๊ฒฝ

    ๐ก Infra
    - AWS EC2
    - Ubuntu 20.04
    - nginx 1.18.0
    - Jenkins 2.289.2

    ๐ก Frontend - Android Native
    - Android Studio Bumblebee | 2021.1.1 Patch 3
    - MVVM Pattern
    - Android SDK MIN 26
    - Android SDK Target 30
    - FCM

    ๐ก Backend - Springboot
    - Spring boot 2.4.5
    - Lombok
    - JPA
    - QueryDSL
    - Gradle 7.4.1
    - JAVA 8


### ๐ Frontend [Android APK ์ค์น ๋ฐฉ๋ฒ] [์ฐธ๊ณ ](https://sbnet.co.kr/install-apk-on-android/)

    1. D201 ๋ฐฐํฌ์ฌ์ดํธ์์ APKํ์ผ์ ๋ค์ด๋ก๋ํฉ๋๋ค.

    2. ๋ค์ด๋ก๋ ๋ฐ์ APK ํ์ผ์ ์ฌ์ฉ์ ํด๋ํฐ์ ์๋ก๋ํฉ๋๋ค.
    - ๊ธฐ์ข์ ๋ฐ๋ผ ๋ฐฉ์์ด ๋ค๋ฅผ ์ ์์ต๋๋ค. 

    3. ํด๋ํฐ์์ APK ํ์ผ์ ์ฐพ์ ํด๋ฆญ ํ ์ค์น๋ฅผ ์งํํด์ฃผ์ธ์.


### ๐ Frontend [Android ์คํ ๋ฐฉ๋ฒ ๋ฐ ์ฑ ๋น๋] [์ค์น๊ฒฝ๋ก](https://developer.android.com/studio?gclid=Cj0KCQjw1a6EBhC0ARIsAOiTkrHs2pne0fbirqMfuaqgSYhktBtCr_y7qyEZ9YptQ6pHlX8BuYxiIAEaAmIIEALw_wcB&gclsrc=aw.ds)

    1. Android Studio๋ฅผ ์ค์นํฉ๋๋ค.

    โ ๋ค์ด๋ก๋ ์, Android Studio Bumblebee 2021.1.1 Patch 3 Windows ๋ฒ์ ์ ๊ถ์ฅ๋๋ฆฝ๋๋ค.
    
    2. Android Studio๋ฅผ ์คํ ํ, File > Open์์ Client ํ๋ก์ ํธ๋ฅผ ์ ํํ ํ 'OK'๋ฅผ ํด๋ฆญํด์ฃผ์ธ์.
    
    3.Local ์๋ฒ์ธ ๊ฒฝ์ฐ์๋ ์ ์ ์๋ฒ ์ฃผ์๋ฅผ ๋ณ๊ฒฝํด์ฃผ์ธ์.
    - app > java > com.ssafy.withssafy > config > ApplicationClass.kt ๊ฒฝ๋ก๋ก ์ด๋ํ์ฌ SERVER_URL ๊ฐ์ ๋ณธ์ธ์ IP์ฃผ์๋ก ๋ณ๊ฒฝํด์ฃผ์ธ์.
    โ ๋จ, ์ ์ํ๋ ค๋ ํด๋ํฐ๊ณผ ์คํํ๋ ค๋ ํ๋ก์ ํธ์ IP์ฃผ์๊ฐ ๋์ผํด์ผํฉ๋๋ค.

    4. Android Version 8.0, API Level 28 ์ด์์ธ ๊ธฐ์ข ์ฐ๊ฒฐ

    5. ์คํํ  ํด๋ํฐ์ ์ ํํ ํ RUN์ ํด๋ฆญํด ๋น๋ํด์ฃผ์ธ์

### ๐ ํด๋ํฐ ์ฐ๊ฒฐ ๋ฐฉ๋ฒ

    1. ์๋ฎฌ๋ ์ดํฐ ์ฐ๊ฒฐ ๋ฐฉ์
    
    - ์๋๋ก์ด๋ ์คํ๋์ค ์ฐ์ธก ์๋จ์ AVD Manager๋ฅผ ํด๋ฆญํฉ๋๋ค
    - ์ข์ธก ํ๋จ Create Virtual Device๋ฅผ ํด๋ฆญํฉ๋๋ค.
    - Phone ๋ฉ๋ด์์ ์์ ์ด ์ํ๋ ๊ธฐ์ข์ ํ๋ ์ ํํ ํ, NEXT๋ฅผ ํฉ๋๋ค
    - ์ค์นํ๊ณ ์ ํ๋ API Level์ ์ ํํ ํ, NEXT๋ฅผ ํฉ๋๋ค.
    - Device ์ด๋ฆ์ ์ค์ ํ ํ, Finishํ๋ฉด AVD๊ฐ ์์ฑ๋ฉ๋๋ค.
    - ์์ฑ๋ ์๋ฎฌ๋ ์ดํฐ๋ฅผ ์คํํ ํ, ์ฌ์ฉํ์๋ฉด ๋ฉ๋๋ค.

    2. ์๋๋ก์ด๋ํฐ ์ฐ๊ฒฐ ๋ฐฉ์

    - USB ์ผ์ด๋ธ์ ์ด์ฉํด ํด๋ํฐ๊ณผ ์ปดํจํฐ๋ฅผ ์ฐ๊ฒฐํฉ๋๋ค.
    - ํธ๋ํฐ ๋ด ์ค์  > ํด๋์ ํ ์ ๋ณด > ์ํํธ์จ์ด ์ ๋ณด > ๋น๋๋ฒํธ๋ฅผ 7๋ฒ ํญํฉ๋๋ค.
    - ํธ๋ํฐ ๋ด ์ค์  > ํด๋์ ํ ์ ๋ณด ๋ฐ์ ๊ฐ๋ฐ์ ์ต์์์ USB ๋๋ฒ๊น์ ํ์ฉํฉ๋๋ค.
    - ๋น๋ ํ, ์ฑ์ ์คํํด ์ฌ์ฉํ์๋ฉด ๋ฉ๋๋ค.


### ๐ [E-R Diagram](./withssafyerd.png)

### ๐ ํ๋ก์ ํธ ๋ด ์ธ๋ถ์๋น์ค ์ ๋ณด

### ๐ [DataBase ๋คํ ํ์ผ ์ต์ ๋ณธ](./Dump20220517.sql)

### ๐ [์์ฐ์๋๋ฆฌ์ค](./์ฌ์ฉ์์๋๋ฆฌ์ค.pdf)

    



SKRIVACI – ANDROID PROJEKAT

Ovo je Android Studio projekat prenet iz dostavljenog VB6 programa.
Aplikacija je predviđena za rad lokalno na telefonu i ne traži internet.

KAKO NAPRAVITI APK:
1. Instalirati Android Studio na Windows računar.
2. Raspakovati ZIP arhivu.
3. U Android Studio izabrati Open i otvoriti folder SKRIVACI_ANDROID.
4. Sačekati da Gradle završi Sync. Ako zatraži Android SDK 35, prihvatiti instalaciju.
5. Izabrati Build > Build Bundle(s) / APK(s) > Build APK(s).
6. APK se nalazi u app/build/outputs/apk/debug/app-debug.apk.

NAPOMENA O OBRAČUNU:
Prenete su formule iz VB6 događaja Command1_Click i Command2_Click:
- po dimenzijama: UD = ceil(obim * 4), GIPS = ceil((obim / odnos) / 2)
- po obimu: UD = ceil(ceil(obim) * 4 / 3), GIPS = ceil((ceil(obim) / odnos) / 2)
- odnos = 120 / (širina + visina + čelo); ako je odnos manji od 2, koristi se 1.
Rezultati i lista soba ostaju sačuvani i nakon zatvaranja aplikacije. Dugme NOVI OBRAČUN briše zbir.

Projekat je izvorni Android Studio projekat, nije već kompajliran APK.
U ovom okruženju nije instaliran Android SDK/Gradle, pa APK nije moguće lokalno izgraditi.

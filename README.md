# 🏫 EduGest — Système de Gestion Scolaire

Application Java Swing de gestion scolaire complète.

## ⚡ Lancement rapide

```bash
java -jar EduGest.jar
```

Ou double-cliquez sur `EduGest.jar` si Java est installé.

## 🔑 Comptes par défaut

| Rôle | Identifiant | Mot de passe | Accès |
|------|------------|--------------|-------|
| 👨‍💼 Directeur | `admin` | `admin123` | Tout |
| 📝 Secrétaire | `secretaire` | `secret123` | Élèves + Classes |
| 💰 Trésorier | `tresorier` | `tresor123` | Paiements + Rapports |

## 🏗️ Architecture

```
src/main/java/com/school/
├── Main.java                    ← Point d'entrée
├── config/
│   └── DatabaseManager.java     ← Connexion H2 (Singleton)
├── model/
│   ├── User.java                ← Utilisateur
│   ├── Student.java             ← Élève
│   ├── Payment.java             ← Paiement
│   ├── SchoolClass.java         ← Classe scolaire
│   └── Role.java                ← Rôles (ADMIN/SECRETAIRE/TRESORIER)
├── repository/
│   ├── UserRepository.java      ← CRUD utilisateurs
│   ├── StudentRepository.java   ← CRUD élèves
│   ├── PaymentRepository.java   ← CRUD paiements
│   └── ClassRepository.java     ← CRUD classes
├── service/
│   ├── AuthService.java         ← Authentification
│   ├── StudentService.java      ← Logique métier élèves
│   ├── PaymentService.java      ← Logique métier paiements
│   ├── ClassService.java        ← Logique métier classes
│   └── SessionManager.java      ← Session courante (Singleton)
└── ui/
    ├── LoginWindow.java         ← Fenêtre de connexion
    ├── MainWindow.java          ← Fenêtre principale + navigation
    ├── components/
    │   ├── AppTheme.java        ← Thème visuel global
    │   ├── Sidebar.java         ← Barre navigation latérale
    │   └── StatCard.java        ← Carte statistiques
    └── panels/
        ├── LoginPanel.java      ← Écran de connexion
        ├── DashboardPanel.java  ← Tableau de bord
        ├── StudentPanel.java    ← Gestion des élèves
        ├── ClassPanel.java      ← Gestion des classes
        ├── PaymentPanel.java    ← Gestion des paiements
        ├── ReportsPanel.java    ← Rapports & statistiques
        ├── UsersPanel.java      ← Gestion des utilisateurs
        └── SettingsPanel.java   ← Paramètres & profil
```

## 🧱 Principes de conception

- **Clean Code** : noms explicites, méthodes courtes, responsabilité unique
- **Architecture en couches** : Model → Repository → Service → UI
- **Patterns** : Singleton (DB, Session), Repository, Service Layer
- **Sécurité** : mots de passe hachés SHA-256, accès par rôle
- **Base de données** : H2 embarquée (aucune installation requise)

## 🔧 Recompiler depuis les sources

### Sur Windows (PowerShell)
```powershell
# Créer le répertoire de sortie
mkdir out -ErrorAction SilentlyContinue

# Compiler (utilise EduGest.jar comme classpath si h2.jar n'est pas présent)
javac -cp "EduGest.jar;." -d out -encoding UTF-8 (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })

# Créer le JAR
jar cfm EduGest.jar src/main/resources/META-INF/MANIFEST.MF -C out .
```

### Sur Linux / macOS / Bash
```bash
# Avec JDK 17+
javac -cp EduGest.jar:h2.jar -d out -encoding UTF-8 $(find src -name "*.java")
jar cfm EduGest.jar MANIFEST.MF -C out .
```

## 📋 Prérequis

- Java 17 ou supérieur (JRE suffit pour lancer, JDK pour recompiler)
- Aucune autre dépendance — H2 est inclus dans le JAR

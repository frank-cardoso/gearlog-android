# GearLog - Gestão de Manutenção Automotiva 🚗

Projeto desenvolvido para a disciplina de ABP (Aprendizagem Baseada em Projetos) na **UniSatc**. O objetivo é auxiliar proprietários de veículos usados e antigos no controle rigoroso de manutenções preventivas e corretivas.

## 🚀 Funcionalidades Principais

* **Dashboard:** Resumo de gastos e alertas de manutenções próximas.
* **Gestão de Veículos:** Cadastro e edição de múltiplos carros (CRUD 1).
* **Histórico Técnico:** Registro detalhado de manutenções com foco em marcas de peças (CRUD 1).
* **Inventário de Peças:** Lista de desejos e estoque de peças adquiridas (CRUD 2).
* **Assistente de Diagnóstico:** Integração com IA para sugestão de causas mecânicas.

## 🛠️ Tecnologias Utilizadas

* **Android Nativo:** Kotlin com Jetpack Compose.
* **Arquitetura:** MVVM (Model-View-ViewModel).
* **Backend:** API REST desenvolvida em Spring Boot (Java/PostgreSQL).
* **Comunicação:** Retrofit & Gson.
* **Injeção de Dependência:** (Opcional - ex: Hilt ou Koin).

## 📋 Requisitos para Rodar o Projeto

* Android Studio Ladybug (ou superior).
* Dispositivo Android com API 26 (Android 8.0) ou superior.
* Conexão com a internet para sincronização com o Backend.

## ✅ Como Executar Localmente (Obrigatório)

### Pré-requisitos

* **Android Studio** Ladybug (ou superior).
* **JDK 17** instalado (recomendado pela configuração do projeto).
* **Android SDK** com **API 36** e **Build Tools** compatíveis.
* **Dispositivo ou Emulador** com **API 26+**.

### Passo a passo

1. **Clone o repositório** e abra a pasta do projeto no Android Studio.
2. **Configure o SDK** em `local.properties` (o Android Studio costuma gerar automaticamente):

```properties
sdk.dir=C\\:\\Users\\<seu-usuario>\\AppData\\Local\\Android\\Sdk
```

3. **Sincronize o Gradle** (`File > Sync Project with Gradle Files`).
4. **Execute o app** com um dispositivo/emulador conectado (`Run > Run 'app'`).

### Opção via terminal (Windows)

```powershell
C:\Users\frank\AndroidStudioProjects\GearLog\gradlew.bat assembleDebug
C:\Users\frank\AndroidStudioProjects\GearLog\gradlew.bat installDebug
```

> Observacao: o `installDebug` requer um dispositivo/emulador conectado.

## 👥 Integrantes do Grupo

* **Frank Cardoso** - [frank-cardoso](https://github.com/frank-cardoso)
* **João Miguel** - [JoaoMiguelRita](https://github.com/JoaoMiguelRita)


## 📱 Integrações Nativas (Hardware/OS)

* **Câmera:** Captura de fotos dos veículos e comprovantes de manutenção.
* **Notificações:** Alertas de revisões preventivas baseadas em data/KM.
* **Biometria:** Autenticação segura para acesso ao perfil do usuário.
* **API de Compartilhamento:** Exportação de listas de peças para fornecedores.

---
*Projeto acadêmico focado em resolver problemas reais de entusiastas automotivos.*
# GearLog - Gestão de Manutenção Automotiva 🚗

Projeto desenvolvido para a disciplina de ABP (Aprendizagem Baseada em Projetos) na **UniSatc**. O objetivo é auxiliar proprietários de veículos usados e antigos no controle rigoroso de manutenções preventivas e corretivas.
https://gamma.app/docs/Sistema-de-Gestao-de-Manutencao-Automotiva-o70l6jhjo6gz2za?mode=present#card-xl6fakddwchjgit

## 🚀 Funcionalidades Principais

* **Dashboard:** Resumo de gastos, quilometragem e visualização rápida dos últimos registros.
* **Gestão de Veículos:** Cadastro, edição e exclusão de múltiplos veículos com suporte a fotos.
* **Histórico Técnico:** Registro detalhado de manutenções e upgrades (modificações).
* **Exportação de Relatórios:** Geração de relatórios em PDF do histórico do veículo para compartilhamento.
* **Inventário de Peças:** Gestão de peças e lista de desejos (Wishlist).
* **Perfil de Usuário:** Personalização de experiência e configurações de tema (Dark/Light).

## 🛠️ Tecnologias Utilizadas

* **Android Nativo:** Kotlin com Jetpack Compose para uma UI moderna e declarativa.
* **Arquitetura:** MVVM (Model-View-ViewModel) com StateFlow para gestão de estado reativa.
* **Backend & Database:** Firebase (Authentication, Firestore para dados NoSQL e Storage para imagens).
* **Navegação:** Jetpack Navigation Compose.
* **Carregamento de Imagens:** Coil.
* **Persistência Local:** DataStore Preferences para configurações de tema.

## 📋 Requisitos para Rodar o Projeto

* Android Studio Ladybug (ou superior).
* Dispositivo Android ou Emulador com API 26 (Android 8.0) ou superior.
* Conexão com a internet para sincronização com o Firebase.
* **Arquivo de Configuração:** É obrigatório possuir o arquivo `google-services.json` na pasta `/app` do projeto para a integração com o Firebase funcionar.

## ✅ Como Executar Localmente (Obrigatório)

### Pré-requisitos

* **Android Studio** Ladybug (ou superior).
* **JDK 17** instalado (recomendado pela configuração do projeto).
* **Android SDK** com **API 36** e **Build Tools** compatíveis.
* **Firebase Config:** O arquivo `google-services.json` deve ser solicitado aos desenvolvedores ou configurado via console do Firebase.

### Passo a passo

1. **Clone o repositório** e abra a pasta do projeto no Android Studio.
2. **Adicione o Firebase:** Cole o arquivo `google-services.json` dentro do diretório `app/`.
3. **Sincronize o Gradle** (`File > Sync Project with Gradle Files`).
4. **Execute o app** com um dispositivo/emulador conectado (`Run > Run 'app'`).

## 👨‍🏫 Espaço do Professor

Caso encontre qualquer dificuldade no primeiro acesso ou bloqueio de autenticação durante a correção:
* **Contato:** Frank Cardoso
* **Telefone/WhatsApp:** (48) 999238-378
* **GitHub:** [frank-cardoso](https://github.com/frank-cardoso)

## 👥 Integrantes do Grupo

* **Frank Cardoso** - [frank-cardoso](https://github.com/frank-cardoso)
* **João Miguel** - [JoaoMiguelRita](https://github.com/JoaoMiguelRita)
* **Gustavo de Freitas Cardoso** - [GustavodeFreitasCardoso](https://github.com/GustavodeFreitasCardoso)
* **Gustavo Nunes** - [ogustavonunes](https://github.com/ogustavonunes)

## 📱 Integrações Nativas (Hardware/OS)

* **Câmera:** Captura de fotos dos veículos e peças através da integração com o sistema.
* **Biometria:** Suporte a autenticação biométrica (Digital/Rosto) para acesso seguro.
* **Sistema de Arquivos:** Geração e compartilhamento de arquivos PDF (Relatórios).
* **Temas do Sistema:** Suporte completo a Modo Escuro (Dark Mode) e Modo Claro.

---
*Projeto acadêmico focado em resolver problemas reais de entusiastas automotivos.*

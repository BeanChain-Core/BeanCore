# BeanPack-Java

**BeanPack-Java** is the official Java SDK for the **BeanChain** blockchain network. It provides core transaction models, cryptographic utilities, wallet structures, and reusable tools used across all Java-based BeanChain projects — including validator nodes, contract execution nodes, reward engines, and developer tools.

This package is maintained by the **BeanChain Core Team** under **Outlandish Tech**, a division of **Outlandish Creative LLC**.

> Current Version: `0.0.1`  
> Actively maintained. Java is the first supported language, with **Go and other SDKs in progress**. If you're interested in helping bring multi-language SDK support to BeanChain, [reach out to the team](#contact).

---

## Designed For

BeanPack-Java is built for modular integration with:

- **BeanNode** – Core validator node
- **CEN** – Contract Execution Node
- **RN** – Reward Node
- Wallet & indexer utilities
- Sidechains & modular custom nodes

---

## What's Included

### Core Modules

#### `block`
- `Block.java` – Block structure and Merkle root calculation

#### `crypto`
- `SHA256TransactionSigner.java` – ECDSA signing (secp256k1)
- `TransactionVerifier.java` – Signature and transaction validator
- `WalletGenerator.java` – Key pair generator, public key → address conversion

#### `models`
- `StateWallet.java` – L1 wallet (nonce + balance)
- `Layer2Wallet.java` – L2 wallet for fungible tokens

#### `txs` (Transaction Types)
- `TX.java` – Core transaction model
- `MintTX.java` – Layer 2 token minting
- `TokenTX.java` – Layer 2 token transfer
- `TokenCENTX.java` – Contract-issued L2 TX
- `StakeTX.java` – Stake TX used in internal contracts
- `AirdropTX.java` – RN-issued reward or drip TX
- `CENCALL.java` – A contract call sent to and handled by a CEN (can include a FundedCallTX or StakeTX, FUNDEDCENCALL)
- `FundedCallTX.java` – A TX that triggers a CENCALL to be built and sent with this TX inside of it for funded CENCALLs


#### `utils`
- `beantoshinomics.java` – BEAN ↔ beantoshi conversion
- `hex.java` – Hex encode/decode utility

#### `wizard`
- `wizard.java` – Lightweight encryption used for local key configs  
  *(This will evolve into a local wizard key manager.)*

#### `security`
- `SecuritySetup.java` – Bouncy Castle provider initializer

#### *Work-In-Progress*
- `CallManager.java` – (CENCALL processor — WIP, not yet active)

---

## How to Use

BeanPack-Java is hosted via GitHub Packages and can be added to any Maven-based project.

### Step 1: Add the GitHub Packages repository

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/BeanChain-Core/BeanPack-Java</url>
  </repository>
</repositories>
```

### Step 2: Add the dependency

```xml
<dependency>
  <groupId>com.beanchain</groupId>
  <artifactId>bean-pack</artifactId>
  <version>0.0.1</version>
</dependency>
```

> Check [Releases](https://github.com/BeanChain-Core/BeanPack-Java/releases) for the latest version.

### Step 3: Add GitHub credentials

In your `~/.m2/settings.xml`:

```xml
<servers>
  <server>
    <id>github</id>
    <username>YourGitHubUsername</username>
    <password>ghp_your_personal_access_token</password>
  </server>
</servers>
```

> Your token must include `read:packages` and `repo` scopes.

---

## SDK Roadmap

Java is the first official SDK — but more are on the way.

We are currently working on:
- **BeanPack-Go** – full Go SDK for lightweight tools and headless embedded nodes
- **Planned SDKs** – Python and JavaScript versions for dApps, explorers, and web tooling

> Interested in helping build SDKs in your preferred language? Let’s collaborate!

---

## License

MIT License — see [`LICENSE`](LICENSE)

---

## Contact

**Email:** team@limabean.xyz  
**Org:** [github.com/BeanChain-Core](https://github.com/BeanChain-Core)

Crafted by the **BeanChain Core Team**  
Under **Outlandish Tech**, powered by **Outlandish Creative LLC**



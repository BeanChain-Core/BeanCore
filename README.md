# BeanCore

**BeanCore** is the official SDK for the BeanChain blockchain network. It provides core transaction models, cryptographic utilities, wallet state structures, and reusable tools shared across all (*Java*) BeanChain projects — including nodes, contract runners, and developer tools.

This SDK is designed for modular integration with:
- *Java Built BeanChain Applications*
- BeanNode (validator node)
- Contract Execution Node (CEN)
- Reward Node (RN)
- Indexers and future sidechain modules

---

## What's Included

This package currently includes the following classes and modules:

### Block
- `Block.java`: Core block structure and Merkle logic

### ~CENCALL (Contract Execution Call Logic)~ *WIP THESE COMPONENTS ARE NOT USABLE IN CURRENT STATE*
- ~`CallManager.java`: General contract call processor~

### Crypto
- `SHA256TransactionSigner.java`: Signs SHA-256 hashes using ECDSA over secp256k1
- `TransactionVerifier.java`: Verifies wallet signature and transaction authenticity
- `WalletGenerator.java`: Key pair generation, public key derivation, and address formatting

### Models
- `Layer2Wallet.java`: Stores wallet balances for Layer 2 tokens 
- `StateWallet.java`: Stores nonce and balance information for Layer 1 wallets

### Security
- `SecuritySetup.java`: Initializes Bouncy Castle cryptographic provider

### TXs (Transaction Types)
- `TX.java`: Main Layer 1 transaction structure
- `MintTX.java`: Specialized TX for minting Layer 2 tokens
- `TokenTX.java`: Specialized TX for sending layer 2 tokens
- `TokenCENTX.java`: Specialized TX for CEN initiated layer 2 TX
- `StakeTX.java`: Specialized TX for internal hardcoded Stake contract logic
- `AirdropTX.java`: Specific TX model used by the team RN for reward/drip airdrops 

### Utils
- `beantoshinomics.java`: Converts between BEAN and beantoshi (smallest unit)
- `hex.java`: Hex encoding/decoding utility

### Wizard
- `wizard.java`: Utility  *USED AS LIGHT ENCRYPTION FOR NODE CONFIG, WILL BE UPDATED, AND WILL BE USED TO CREATE AN EASY DOWNLOAD LOCAL WIZARD KEY SAVER*

---

## How to Use

BeanCore is published via GitHub Packages and can be added to any Maven-based Java project.

### Step 1: Add the GitHub Packages repository

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/BeanChain-Core/BeanCore</url>
  </repository>
</repositories>
```

### Step 2: Add the dependency

```xml
<dependency>
  <groupId>com.beanchain</groupId>
  <artifactId>bean-core</artifactId>
  <version>0.2.3</version>
</dependency>
```
CHECK THE LATEST VERSION RELEASE

### Step 3: Add your GitHub token to Maven settings

In your `~/.m2/settings.xml` file:

```xml
<servers>
  <server>
    <id>github</id>
    <username>YourGitHubUsername</username>
    <password>ghp_your_personal_access_token</password>
  </server>
</servers>
```

> Your token must have `read:packages` and `repo` scopes.

---

## License

This project is open-source under the MIT License.  
See the [`LICENSE`](LICENSE) file for details.

---

## Contact

For support, integration help, or contribution inquiries, contact the BeanChain core team:

**Email:** team@limabean.xyz



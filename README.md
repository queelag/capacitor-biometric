# @aracna/capacitor-biometric

Secure biometric implementation with asymmetric encryption capabilities.

## Install

```bash
npm install @aracna/capacitor-biometric
npx cap sync
```

## API

<docgen-index>

* [`isAvailable()`](#isavailable)
* [`prompt(...)`](#prompt)
* [`createAsymmetricKeys()`](#createasymmetrickeys)
* [`createSymmetricKey()`](#createsymmetrickey)
* [`readPublicKey()`](#readpublickey)
* [`deleteAsymmetricKeys()`](#deleteasymmetrickeys)
* [`deleteSymmetricKey()`](#deletesymmetrickey)
* [`writeData(...)`](#writedata)
* [`readData(...)`](#readdata)
* [`deleteData(...)`](#deletedata)
* [`hasData(...)`](#hasdata)
* [`areAsymmetricKeysCreated()`](#areasymmetrickeyscreated)
* [`isSymmetricKeyCreated()`](#issymmetrickeycreated)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### isAvailable()

```typescript
isAvailable() => Promise<void>
```

--------------------


### prompt(...)

```typescript
prompt({}: BiometricPluginPromptBody) => Promise<void>
```

| Param     | Type                                                                            |
| --------- | ------------------------------------------------------------------------------- |
| **`__0`** | <code><a href="#biometricpluginpromptbody">BiometricPluginPromptBody</a></code> |

--------------------


### createAsymmetricKeys()

```typescript
createAsymmetricKeys() => Promise<CreateAsymmetricKeysResponse>
```

**Returns:** <code>Promise&lt;<a href="#createasymmetrickeysresponse">CreateAsymmetricKeysResponse</a>&gt;</code>

--------------------


### createSymmetricKey()

```typescript
createSymmetricKey() => Promise<void>
```

--------------------


### readPublicKey()

```typescript
readPublicKey() => Promise<Response<string>>
```

**Returns:** <code>Promise&lt;<a href="#response">Response</a>&lt;string&gt;&gt;</code>

--------------------


### deleteAsymmetricKeys()

```typescript
deleteAsymmetricKeys() => Promise<void>
```

--------------------


### deleteSymmetricKey()

```typescript
deleteSymmetricKey() => Promise<void>
```

--------------------


### writeData(...)

```typescript
writeData({}: WriteDataBody) => Promise<void>
```

| Param     | Type                                                    |
| --------- | ------------------------------------------------------- |
| **`__0`** | <code><a href="#writedatabody">WriteDataBody</a></code> |

--------------------


### readData(...)

```typescript
readData({}: ReadDataBody) => Promise<Response<string>>
```

| Param     | Type                                                  |
| --------- | ----------------------------------------------------- |
| **`__0`** | <code><a href="#readdatabody">ReadDataBody</a></code> |

**Returns:** <code>Promise&lt;<a href="#response">Response</a>&lt;string&gt;&gt;</code>

--------------------


### deleteData(...)

```typescript
deleteData({}: DeleteDataBody) => Promise<void>
```

| Param     | Type                                                      |
| --------- | --------------------------------------------------------- |
| **`__0`** | <code><a href="#deletedatabody">DeleteDataBody</a></code> |

--------------------


### hasData(...)

```typescript
hasData({}: HasDataBody) => Promise<Response<boolean>>
```

| Param     | Type                                                |
| --------- | --------------------------------------------------- |
| **`__0`** | <code><a href="#hasdatabody">HasDataBody</a></code> |

**Returns:** <code>Promise&lt;<a href="#response">Response</a>&lt;boolean&gt;&gt;</code>

--------------------


### areAsymmetricKeysCreated()

```typescript
areAsymmetricKeysCreated() => Promise<Response<boolean>>
```

**Returns:** <code>Promise&lt;<a href="#response">Response</a>&lt;boolean&gt;&gt;</code>

--------------------


### isSymmetricKeyCreated()

```typescript
isSymmetricKeyCreated() => Promise<Response<boolean>>
```

**Returns:** <code>Promise&lt;<a href="#response">Response</a>&lt;boolean&gt;&gt;</code>

--------------------


### Interfaces


#### BiometricPluginPromptBody

| Prop                       | Type                 |
| -------------------------- | -------------------- |
| **`confirmationRequired`** | <code>boolean</code> |
| **`description`**          | <code>string</code>  |
| **`negativeButtonText`**   | <code>string</code>  |
| **`subtitle`**             | <code>string</code>  |
| **`title`**                | <code>string</code>  |


#### CreateAsymmetricKeysResponse

| Prop            | Type                |
| --------------- | ------------------- |
| **`publicKey`** | <code>string</code> |


#### Response

| Prop        | Type           |
| ----------- | -------------- |
| **`value`** | <code>T</code> |


#### WriteDataBody

| Prop        | Type                |
| ----------- | ------------------- |
| **`key`**   | <code>string</code> |
| **`value`** | <code>string</code> |


#### ReadDataBody

| Prop      | Type                |
| --------- | ------------------- |
| **`key`** | <code>string</code> |


#### DeleteDataBody

| Prop      | Type                |
| --------- | ------------------- |
| **`key`** | <code>string</code> |


#### HasDataBody

| Prop      | Type                |
| --------- | ------------------- |
| **`key`** | <code>string</code> |

</docgen-api>

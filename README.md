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
isAvailable() => any
```

**Returns:** <code>any</code>

--------------------


### prompt(...)

```typescript
prompt({}: BiometricPluginPromptBody) => any
```

| Param     | Type                                                                            |
| --------- | ------------------------------------------------------------------------------- |
| **`__0`** | <code><a href="#biometricpluginpromptbody">BiometricPluginPromptBody</a></code> |

**Returns:** <code>any</code>

--------------------


### createAsymmetricKeys()

```typescript
createAsymmetricKeys() => any
```

**Returns:** <code>any</code>

--------------------


### createSymmetricKey()

```typescript
createSymmetricKey() => any
```

**Returns:** <code>any</code>

--------------------


### readPublicKey()

```typescript
readPublicKey() => any
```

**Returns:** <code>any</code>

--------------------


### deleteAsymmetricKeys()

```typescript
deleteAsymmetricKeys() => any
```

**Returns:** <code>any</code>

--------------------


### deleteSymmetricKey()

```typescript
deleteSymmetricKey() => any
```

**Returns:** <code>any</code>

--------------------


### writeData(...)

```typescript
writeData({}: WriteDataBody) => any
```

| Param     | Type                                                    |
| --------- | ------------------------------------------------------- |
| **`__0`** | <code><a href="#writedatabody">WriteDataBody</a></code> |

**Returns:** <code>any</code>

--------------------


### readData(...)

```typescript
readData({}: ReadDataBody) => any
```

| Param     | Type                                                  |
| --------- | ----------------------------------------------------- |
| **`__0`** | <code><a href="#readdatabody">ReadDataBody</a></code> |

**Returns:** <code>any</code>

--------------------


### deleteData(...)

```typescript
deleteData({}: DeleteDataBody) => any
```

| Param     | Type                                                      |
| --------- | --------------------------------------------------------- |
| **`__0`** | <code><a href="#deletedatabody">DeleteDataBody</a></code> |

**Returns:** <code>any</code>

--------------------


### hasData(...)

```typescript
hasData({}: HasDataBody) => any
```

| Param     | Type                                                |
| --------- | --------------------------------------------------- |
| **`__0`** | <code><a href="#hasdatabody">HasDataBody</a></code> |

**Returns:** <code>any</code>

--------------------


### areAsymmetricKeysCreated()

```typescript
areAsymmetricKeysCreated() => any
```

**Returns:** <code>any</code>

--------------------


### isSymmetricKeyCreated()

```typescript
isSymmetricKeyCreated() => any
```

**Returns:** <code>any</code>

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

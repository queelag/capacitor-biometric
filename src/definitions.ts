export interface Response<T> {
  value: T;
}

export interface BiometricPluginPromptBody {
  confirmationRequired?: boolean;
  description?: string;
  negativeButtonText?: string;
  subtitle?: string;
  title?: string;
}

export interface CreateAsymmetricKeysResponse {
  publicKey: string;
}

export interface WriteDataBody {
  key: string;
  value: string;
}

export interface ReadDataBody {
  key: string;
}

export interface DeleteDataBody {
  key: string;
}

export interface HasDataBody {
  key: string;
}

export interface BiometricPlugin {
  isAvailable(): Promise<void>;
  prompt({}: BiometricPluginPromptBody): Promise<void>;
  createAsymmetricKeys(): Promise<CreateAsymmetricKeysResponse>;
  createSymmetricKey(): Promise<void>;
  readPublicKey(): Promise<Response<string>>;
  deleteAsymmetricKeys(): Promise<void>;
  deleteSymmetricKey(): Promise<void>;
  writeData({}: WriteDataBody): Promise<void>;
  readData({}: ReadDataBody): Promise<Response<string>>;
  deleteData({}: DeleteDataBody): Promise<void>;
  hasData({}: HasDataBody): Promise<Response<boolean>>;
  areAsymmetricKeysCreated(): Promise<Response<boolean>>;
  isSymmetricKeyCreated(): Promise<Response<boolean>>;
}

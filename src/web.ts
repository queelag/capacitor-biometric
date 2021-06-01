import { WebPlugin } from '@capacitor/core';
import {
  BiometricPlugin,
  BiometricPluginPromptBody,
  CreateAsymmetricKeysResponse,
  DeleteDataBody,
  HasDataBody,
  ReadDataBody,
  Response,
  WriteDataBody,
} from './definitions';

export class BiometricWeb extends WebPlugin implements BiometricPlugin {
  async isAvailable(): Promise<void> {
    throw new Error();
  }

  async prompt({}: BiometricPluginPromptBody): Promise<void> {
    throw new Error();
  }

  async createAsymmetricKeys(): Promise<CreateAsymmetricKeysResponse> {
    throw new Error();
  }

  async createSymmetricKey(): Promise<void> {
    throw new Error();
  }

  async readPublicKey(): Promise<Response<string>> {
    throw new Error();
  }

  async deleteAsymmetricKeys(): Promise<void> {
    throw new Error();
  }

  async deleteSymmetricKey(): Promise<void> {
    throw new Error();
  }

  async writeData({}: WriteDataBody): Promise<void> {
    throw new Error();
  }

  async readData({}: ReadDataBody): Promise<Response<string>> {
    throw new Error();
  }

  async deleteData({}: DeleteDataBody): Promise<void> {
    throw new Error();
  }

  async hasData({}: HasDataBody): Promise<Response<boolean>> {
    throw new Error();
  }

  async areAsymmetricKeysCreated(): Promise<Response<boolean>> {
    throw new Error();
  }

  async isSymmetricKeyCreated(): Promise<Response<boolean>> {
    throw new Error();
  }
}

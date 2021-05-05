declare module '@capacitor/core' {
  interface PluginRegistry {
    Biometric: BiometricPlugin;
  }
}

export interface BiometricPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}

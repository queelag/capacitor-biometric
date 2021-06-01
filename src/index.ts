import { registerPlugin } from '@capacitor/core';

import type { BiometricPlugin } from './definitions';

const Biometric = registerPlugin<BiometricPlugin>('Biometric', {
  web: () => import('./web').then(m => new m.BiometricWeb()),
});

export * from './definitions';
export { Biometric };

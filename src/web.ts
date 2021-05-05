import { WebPlugin } from '@capacitor/core';
import { BiometricPlugin } from './definitions';

export class BiometricWeb extends WebPlugin implements BiometricPlugin {
  constructor() {
    super({
      name: 'Biometric',
      platforms: ['web'],
    });
  }

  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

const Biometric = new BiometricWeb();

export { Biometric };

import { registerWebPlugin } from '@capacitor/core';
registerWebPlugin(Biometric);

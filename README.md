# Looking For A Reference Implementation

... of Shamir's Secret Sharing - probably using GF(256) to share byte array secrets.

## Questions To crypto.stackexchange.com

### Reference Implementation Of Shamir's Secret Sharing

**Reference implementation of Shamir's Secret Sharing**

Is there an implementation of Shamir's Secret Sharing that can be regarded as "canonical" or "reference" implementation?

The above question is pretty vague. I have more details in mind, but some of them might be misleading or base on false assumptions. So possibly not all of them can be fulfilled or are relevant.

* I'm looking for an implementation of Shamir's Secret Sharing **only** without additional (security) features.
* The input should be an array of bytes as secret, a threshold t and a number of shares n.
* The output should be n shares, where each share consists of the share number and an array of bytes of the same size as the secret.
* The algorithm should use GF(256). This is based on the following assumptions:
  * When a field other than GF(256) is used for sharing, there is no guarantee that the secret can be reconstructed using GF(256).
  * Requiring the use of GF(256) is enough to ensure that each correct split implementation will be compatible with each other correct join implementation. If this assumption is not complete - what is missing for a full specification of the method?



### Make Shamir's Secret Sharing Verifiable By Publishing One Share?

### Shamir's Secret Sharing: Which Information Needs To Be Shared

## Properties Of A Reference Implementation

* Optionally use simple pseudo random instead of secure random
* Easy-to-understand code, possibly multiple languages

## Ubuntu Package ssss

* Current version: v0.5, January 2006
* Sources: http://point-at-infinity.org/ssss/
* A fork on GitHub: https://github.com/osresearch/ssss
* Input up to 128 bytes
* Additional "diffusion layer" for input >= 8 bytes, added in version 0.2
* When setting a security level, the input start is zero-padded to provide the necessary key length

**sharing "hello", t = 3, n = 5**

    > ssss-split -t 3 -n 5

    Generating shares using a (3,5) scheme with dynamic security level.
    Enter the secret, at most 128 ASCII characters: Using a 40 bit security level.
    WARNING: security level too small for the diffusion layer.
    1-4b816966f2
    2-f3e248c82d
    3-d0064dc2b6
    4-2e579fe101
    5-0db39aeb88
    
    > ssss-combine -t 3

    Enter 3 shares separated by newlines:
    Share [1/3]: 4-2e579fe101
    Share [2/3]: 1-4b816966f2
    Share [3/3]: 5-0db39aeb88
    WARNING: security level too small for the diffusion layer.
    Resulting secret: hello

    > ssss-combine -t 4

    Enter 4 shares separated by newlines:
    Share [1/4]: 1-4b816966f2
    Share [2/4]: 2-f3e248c82d
    Share [3/4]: 3-d0064dc2b6
    Share [4/4]: 4-2e579fe101
    WARNING: security level too small for the diffusion layer.
    Resulting secret: hellw

The implementation contains an additional diffusion feature, see:

https://github.com/osresearch/ssss/blob/master/ssss.c#L439-L444

This is feature is only active when n >= 8 bytes are shared.

## Additional Implementations Out There

* https://github.com/grempe/secrets.js
* https://github.com/iancoleman/shamir
  * As web page https://iancoleman.io/shamir/
  * Pads to 2048(?) bits
  * The share number is not visible
  * All shares start with 0x80
* https://asecuritysite.com/encryption/shamir
  * All shares start with ascii n zero-padded to 3 digits
  * Rest of the share is base-64
  * "hello" results in a 8-byte share
  * Share numbers start at 0
* https://simon-frey.com/s4/
  * Shares are base-64
  * Share number is not visible
  * "hello" results in a 12-byte share
* 

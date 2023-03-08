# Looking For A Reference Implementation

... of Shamir's Secret Sharing - probably using GF(256) to share byte array secrets.

## Questions To crypto.stackexchange.com

### Reference Implementation Of Shamir's Secret Sharing

https://crypto.stackexchange.com/q/105502/108144

**Reference implementation of Shamir's Secret Sharing**

Is there an implementation of [Shamir's Secret Sharing](https://en.wikipedia.org/wiki/Shamir%27s_secret_sharing) that can be regarded as "canonical" (or "reference" or "standard") implementation, so that I can test other implementations to be "standard compliant"?

The above question is pretty vague. I have more details in mind, but some of them might be misleading or base on false assumptions. So possibly not all of them can be fulfilled or are relevant.

* I'm looking for a pure implementation of Shamir's Secret Sharing - pure means without additional (security) features.
* The input should be an array of bytes as secret, a threshold t and a number of shares n.
* The output should be n shares, where each share consists of the share number and an array of bytes of the same size as the secret.
* The algorithm should use GF(256). This is based on the following assumptions:
  * When a field other than GF(256) is used for sharing, there is no guarantee that the secret can be reconstructed using GF(256).
  * Requiring the use of GF(256) is enough to ensure that each correct split implementation will be compatible with each other correct join implementation. If this assumption is not complete - what is missing for a full specification of the method?

The motivation for this question is: I noticed that when I share a secret with implementation A, it is not sure I can reconstruct the secret with implementation B.

For example, "hello" shared with the implementation https://github.com/codahale/shamir has given me the shares

    1-081dea6049
    2-c869462a01
    3-a811c02627
    4-a8a0cc833b
    5-c8d84a8f1d

    // Implemented like:
    Scheme scheme = new Scheme(new SecureRandom(), 5, 3);
    Map<Integer, byte[]> split = scheme.split("hello".getBytes("UTF-8"));

Reconstructing the secret from shares 5, 2, 3 using https://github.com/codahale/shamir works fine, like this:

    Scheme scheme = new Scheme(new SecureRandom(), 5, 3);
    Map<Integer, byte[]> example = Map.of(
            5, java.util.HexFormat.of().parseHex("c8d84a8f1d"),
            2, java.util.HexFormat.of().parseHex("c869462a01"),
            3, java.util.HexFormat.of().parseHex("a811c02627")
    );
    byte[] exampleJoined = scheme.join(example);


But reconstructing the secret from the same shares using the debian package "ssss" (http://point-at-infinity.org/ssss/, version v0.5, January 2006) gives me the byte array 056bcedfa2 (where I would have expected the bytes of "hello", i.e. 68656c6c6f):

    > ssss-combine -t 3 -x -D
    Enter 3 shares separated by newlines:
    Share [1/3]: 5-c8d84a8f1d
    Share [2/3]: 2-c869462a01
    Share [3/3]: 3-a811c02627
    Resulting secret: 056bcedfa2

### Make Shamir's Secret Sharing Verifiable By Dealing Pairs Of Shares?

Can I make Shamir's Secret Sharing verifiable by dealing double shares

... or by simply collecting one additional share in order to have an overdetermined system of equations`?

Example: I want to share a secret with 4 players A/B/C/D, and any 2 players shall be able to recreate the secret. In the standard approach, if A's share is corrupted (or tampered with, e.g. one bit has been changed), it is still possible to reconstruct **a value** from A's and B's share, and without additional information, there is no way to tell that it's not the original secret.





ideally e.g. share 1 - all bytes 0 ?

### Shamir's Secret Sharing: Which Information Needs To Be Shared

### Shamir's Secret Sharing: Do the shared byte arrays contain zero?

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

# Codahale's Java implemention

* https://github.com/codahale/shamir


    Splitting "hello"

    1-081dea6049
    2-c869462a01
    3-a811c02627
    4-a8a0cc833b
    5-c8d84a8f1d

    Joining 5,2,3:

    Example joined to: hello

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

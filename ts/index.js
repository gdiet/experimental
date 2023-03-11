"use strict";
// document utilitiy functions
let input = (id) => document.getElementById(id);
let button = (id) => document.getElementById(id);
// text utility functions
let fromUTF8 = (text) => Array.from(new TextEncoder().encode(text));
// document access
let doc = {
    get secretText() { return input('secretTextInput').value; },
    get secretNumbers() { return input('secretNumbersInput').value; },
    set secretNumbers(s) { input('secretNumbersInput').value = s; }
};
// typed document content
let cont = {
    get secretNumbers() { return doc.secretNumbers.split(',').map(n => parseInt(n) || 0); }
};
// document automation
let aut = {
    fillSecretNumbersFromText: () => doc.secretNumbers = fromUTF8(doc.secretText).join(','),
    fixSecretNumbers: () => doc.secretNumbers = cont.secretNumbers.map(n => Math.max(0, n)).map(n => Math.min(257, n)).join(',')
};
aut.fillSecretNumbersFromText();
button('secretTextToNumbersButton').addEventListener('click', aut.fillSecretNumbersFromText);
input('secretNumbersInput').addEventListener('change', aut.fixSecretNumbers);
// // Dynamic document content functions
// const coefficients = () => {
//   let coefficientsText = byId('staticCoefficients').checked ? byId('userCoefficients').value : byId('generatedCoefficients').innerHTML
//   let coefficientNumbers = coefficientsText.split(',').map(c => Number(c) || 0)
//   let numberOfCoefficients = byId('threshold').value - 1
//   return Array.from({length: numberOfCoefficients}, (_, index) => {
//       if (coefficientNumbers[index] == 0 && index == numberOfCoefficients - 1) return 1
//       return coefficientNumbers[index] < 0 ? 0 : coefficientNumbers[index]
//   })
// }
// const setPolynomialAndGeneratedCoefficientsHtml = () => {
//   let threshold = byId('threshold').value
//   let generatedCoefficients = Array.from({length: threshold - 2}, () => random(0, 257))
//   generatedCoefficients.push(random(1, 257)) // The last coefficient must not be 0.
//   byId('generatedCoefficients').innerHTML = generatedCoefficients.join(',')
//   let html = `P(x) = secret`
//   for (let k = 1; k < threshold; k++) html += ` + ${coefficients()[k-1]}x<sup>${k}</sup>`
//   byId('polynomial').innerHTML = html + ' | (mod 257)'
// }
// setPolynomialAndGeneratedCoefficientsHtml()
// document initialization
// const listen = (id, event, action) => byId(id).addEventListener(event, action)
// writing the document
// const secretTextToNumbersHtml = () =>
//     byId('secretNumbersInput').value = utf8ToBytes(byId('secretTextInput').value).join(',')
// // Document automation
// secretTextToNumbersHtml()
/*

// Helper functions for improved readability
const foldLeft = (array, zero, func) => array.reduce(func, zero)

// Maths for secret sharing and reconstruction
const mod = (a, n) => ((a % n) + n) % n
const inverse = (a, n) => {
    for (let k = 1; k < n; k++) if (mod(k * a, n) == 1) return k
    throw new Error(`${a} is not invertible mod ${n}`)
}
const interpolate = (data, x, n) =>
    foldLeft(data, 0, (r, e1, i) =>
        mod(foldLeft(data, e1.y, (t, e2, j) =>
            i == j ? t : mod(t * (x - e2.x) * inverse(e1.x - e2.x, n), n)
        ) + r, 257)
    )
const exp = (a, e, n) => {
    if (a == 0) return 0
    let factor = e >= 0 ? a : inverse(a, n)
    let result = 1
    for (let k = 1; k <= Math.abs(e); k++) result = mod(result * factor, n)
    return result
}
const calculatePolynomial = (x, coefficients, n) =>
  foldLeft(coefficients, 0, (r, c, i) => mod(r + c * exp(x, i, n), n))


  
// min, max: The minimum is inclusive and the maximum is exclusive
const random = (min, max) => Math.floor(Math.random() * (max - min) + min)


listen('randomCoefficients',        'change', () => setPolynomialAndGeneratedCoefficientsHtml())
listen('staticCoefficients',        'change', () => setPolynomialAndGeneratedCoefficientsHtml())
listen('userCoefficients',          'change', () => setPolynomialAndGeneratedCoefficientsHtml())

listen('numberOfShares', 'change', () => {
    let numberOfSharesRead = Math.floor(Math.abs(Number(byId('numberOfShares').value)))
    byId('numberOfShares').value = Math.min(256, Math.max(byId('threshold').value, numberOfSharesRead))
})
listen('threshold', 'change', () => {
    let thresholdRead = Math.floor(Math.abs(Number(byId('threshold').value)))
    byId('threshold').value = Math.min(byId('numberOfShares').value, Math.max(2, thresholdRead))
    setPolynomialAndGeneratedCoefficientsHtml()
})

listen('createSharesButton', 'click',  () => {
    for (let x = 1; x <= byId('numberOfShares').value; x++) {
        let secretNumbers = byId('secretNumbersInput').value.split(',')
        console.log("share for x", x)
        for (let k = 0; k < secretNumbers.length; k++) {
            let polynomialCoefficients = [secretNumbers[k]].concat(coefficients())
            let y = calculatePolynomial(x, polynomialCoefficients, 257)
            console.log(x,secretNumbers[k],y)
        }
    }
})

// to create a share for x,
// the first coefficient must be the secret,
// the middle coefficients should be random numbers,
// the last coefficient modulus 257 must not be 0.
// the total number of coefficients (including share)
// is the number of shares needed for reconstruction.
// console.log(calculatePolynomial(2, [4,6,3], 257))


// https://developer.mozilla.org/en-US/docs/Web/API/SubtleCrypto/generateKey
// for the signature - consider using a ecc key pair instead of rsa

// const toHex        = byte  => byte.toString(16).padStart(2, '0')
// const toHexSpaced  = array => array.map(byte => toHex(byte)).join(" ")
// const toHexCompact = array => array.map(byte => toHex(byte)).join("")

// const randomByte   = ()         => random(0, 256)
// const randomBytes  = count      => Array.from({length: count}, () => randomByte() )

*/

let math = {
  mod: (a: number, n: number) => ((a % n) + n) % n,
  inverse: (a: number, n: number) => {
    for (let k = 1; k < n; k++) if (math.mod(k * a, n) == 1) return k
    throw new Error(`${a} is not invertible mod ${n}`) 
  },
  exp: (a: number, e: number, n: number) => {
    if (a == 0) return 0
    let factor = e >= 0 ? a : math.inverse(a, n)
    let result = 1
    for (let k = 1; k <= Math.abs(e); k++) result = math.mod(result * factor, n)
    return result
  },
  calculatePolynomial: (x: number, coefficients: number[], n: number) =>
    foldLeft(coefficients, 0, (r, c, i) => math.mod(r + c * math.exp(x, i, n), n)),
}
/*
const interpolate = (data, x, n) => 
    foldLeft(data, 0, (r, e1, i) =>
        mod(foldLeft(data, e1.y, (t, e2, j) =>
            i == j ? t : mod(t * (x - e2.x) * inverse(e1.x - e2.x, n), n)
        ) + r, 257)
    )
*/

// document utilitiy functions
let input  = (id: string) => document.getElementById(id) as HTMLInputElement
let button = (id: string) => document.getElementById(id) as HTMLButtonElement
let radio  = (id: string) => document.getElementById(id) as HTMLInputElement
let span   = (id: string) => document.getElementById(id) as HTMLSpanElement

// text utility functions
let fromUTF8 = (text: string) => Array.from(new TextEncoder().encode(text))

// min, max: The minimum is inclusive and the maximum is exclusive
let random = (min: number, max: number) => Math.floor(Math.random() * (max - min) + min)
let limit = (n: number, min: number, max: number) => Math.min(max - 1, Math.max(min, n))

// nicer type signatures
function foldLeft<A, T>(array: A[], zero: T, func: (a: A, t: T, i: number) => T) {
  return array.reduce((t: T, a: A, i: number) => func(a, t, i), zero)
}

// document access
let doc = {
  get secretText() { return input('secretTextInput').value },
  get secretNumbers() { return input('secretNumbersInput').value },
  set secretNumbers(s: string) { input('secretNumbersInput').value = s },
  get numberOfShares() { return input('numberOfSharesInput').value },
  set numberOfShares(s: string) { input('numberOfSharesInput').value = s },
  get threshold() { return input('thresholdInput').value },
  set threshold(s: string) { input('thresholdInput').value = s },
  get generatedCoefficients() { return span('generatedCoefficientsSpan').innerText },
  set generatedCoefficients(s: string) { span('generatedCoefficientsSpan').innerText = s },
  get staticCoefficients() { return input('staticCoefficientsInput').value },
  set staticCoefficients(s: string) { input('staticCoefficientsInput').value = s },
  get staticCoefficientsSelected() { return radio('staticCoefficientsRadio').checked },
  set polynomial(s: string) { span('polynomialSpan').innerText = s },
}

// typed document content
let cont = {
  get secretNumbers() { return doc.secretNumbers.split(',').map(n => parseInt(n) || 0) },
  set secretNumbers(numbers: number[]) { doc.secretNumbers = numbers.join(',') },
  get numberOfShares() { return parseInt(doc.numberOfShares) },
  set numberOfShares(shares: number) { doc.numberOfShares = String(shares) },
  get threshold() { return parseInt(doc.threshold) },
  set threshold(threshold: number) { doc.threshold = String(threshold) },
  get generatedCoefficients() { return doc.generatedCoefficients.split(',').map(n => parseInt(n)) },
  set generatedCoefficients(coefficients: number[]) { doc.generatedCoefficients = coefficients.join(',') },
  get staticCoefficients() { return doc.staticCoefficients.split(',').map(n => parseInt(n) || 0) },
  set staticCoefficients(coefficients: number[]) { doc.staticCoefficients = coefficients.join(',') },
  get coefficients() { return doc.staticCoefficientsSelected ? this.staticCoefficients : this.generatedCoefficients },
  get cleanedCoefficients() {
    return cont.coefficients.concat(Array(cont.threshold - 1).fill(0)) // Ensure the right size and last != 0
      .slice(0, cont.threshold - 1).map((c, i) => (c == 0 && i == cont.threshold - 2) ? 1 : c)
  }
}

// document automation
let aut = {
  fillSecretNumbersFromText: () => 
    cont.secretNumbers = fromUTF8(doc.secretText),
  fixSecretNumbers: () => 
    cont.secretNumbers = cont.secretNumbers.map(n => limit(n, 0, 257)),
  fixNumerOfShares: () =>
    cont.numberOfShares = limit(cont.numberOfShares, cont.threshold, 257),
  fixThreshold: () =>
    cont.threshold = limit(cont.threshold, 2, cont.numberOfShares + 1),
  generateCoefficients: () => // The last coefficient must not be 0.
    cont.generatedCoefficients = Array.from({length: cont.threshold - 2}, () => random(0, 257)).concat([random(1, 257)]),
  fixStaticCoefficients: () =>
    cont.staticCoefficients = cont.staticCoefficients.map(c => limit(c, 0, 257)),
  displayPolynomial: () =>
    span('polynomialSpan').innerHTML =
      foldLeft(cont.cleanedCoefficients, "P(x) = secret", (c, result, index) => `${result} + ${c}x<sup>${index + 1}</sup>`),
}

const createShares = () => {
  let coefficients = cont.cleanedCoefficients
  let polynomial = foldLeft(coefficients, "P(x) = secret", (_, result, index) => `${result} + c${index + 1}*x^${index + 1}`)
  for (let x = 1; x <= cont.numberOfShares; x++) {
    let share = cont.secretNumbers.map(secret => math.calculatePolynomial(x, [secret].concat(coefficients), 257))
    console.log(polynomial)
    console.log(`P(${x})=${share.join(',')}`)
  }
}

// wire the document functions
aut.fillSecretNumbersFromText()
aut.generateCoefficients()
aut.displayPolynomial()
button('secretTextToNumbersButton').addEventListener('click', aut.fillSecretNumbersFromText)
input('secretNumbersInput').addEventListener('change', aut.fixSecretNumbers)
input('numberOfSharesInput').addEventListener('change', aut.fixNumerOfShares)
input('thresholdInput').addEventListener('change', aut.fixThreshold)
input('thresholdInput').addEventListener('change', aut.generateCoefficients)
input('thresholdInput').addEventListener('change', aut.displayPolynomial)
button('randomCoefficientsRadio').addEventListener('change', aut.generateCoefficients)
button('randomCoefficientsRadio').addEventListener('change', aut.displayPolynomial)
radio('staticCoefficientsRadio').addEventListener('change', aut.generateCoefficients)
radio('staticCoefficientsRadio').addEventListener('change', aut.displayPolynomial)
input('staticCoefficientsInput').addEventListener('change', aut.fixStaticCoefficients)
input('staticCoefficientsInput').addEventListener('change', aut.displayPolynomial)
button('createSharesButton').addEventListener('click', createShares)

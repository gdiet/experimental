// math functions for Shamir's Secret Sharing (mod 257)
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
    foldLeft(coefficients, 0, (c, r, i) => math.mod(r + c * math.exp(x, i, n), n)),
  interpolate: (data: {x: number, y: number}[], x: number, n: number) => 
    foldLeft(data, 0, (e1, r, i) =>
      math.mod(foldLeft(data, e1.y, (e2, t, j) =>
        i == j ? t : math.mod(t * (x - e2.x) * math.inverse(e1.x - e2.x, n), n)
      ) + r, 257)
    ),
}

// document utilitiy functions
let input     = (id: string) => document.getElementById(id) as HTMLInputElement
let button    = (id: string) => document.getElementById(id) as HTMLButtonElement
let radio     = (id: string) => document.getElementById(id) as HTMLInputElement
let span      = (id: string) => document.getElementById(id) as HTMLSpanElement
let div       = (id: string) => document.getElementById(id) as HTMLDivElement
let paragraph = (id: string) => document.getElementById(id) as HTMLParagraphElement

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
  // split secret into shares
  get secretText() { return input('secretTextInput').value },
  get secretNumbers() { return input('secretNumbersInput').value },
  set secretNumbers(s: string) { input('secretNumbersInput').value = s },
  get numberOfShares() { return input('numberOfSharesInput').value },
  set numberOfShares(s: string) { input('numberOfSharesInput').value = s },
  get threshold() { return input('thresholdInput').value },
  set threshold(s: string) { input('thresholdInput').value = s },
  get staticCoefficients() { return input('staticCoefficientsInput').value },
  set staticCoefficients(s: string) { input('staticCoefficientsInput').value = s },
  get staticCoefficientsSelected() { return radio('staticCoefficientsRadio').checked },
  set polynomial(s: string) { span('polynomialSpan').innerText = s },
  set sharesHtml(s: string) { span('shares').innerHTML = s },
  // recover secret from shares
  get availableShares() { return input('availableSharesInput').value },
  set availableShares(s: string) { input('availableSharesInput').value = s },
  set shareInputs(s: string) { div('sharesDiv').innerHTML = s },
  shareInput: (index: number) => input(`share${index}}Input`).value,
  setShareInput: (index: number, s: string) => input(`share${index}}Input`).value = s,
}

// typed document content
let cont = {
  // split secret into shares
  get secretNumbers() { return doc.secretNumbers.split(',').map(n => parseInt(n) || 0) },
  set secretNumbers(numbers: number[]) { doc.secretNumbers = numbers.join(',') },
  get numberOfShares() { return parseInt(doc.numberOfShares) },
  set numberOfShares(shares: number) { doc.numberOfShares = String(shares) },
  get threshold() { return parseInt(doc.threshold) },
  set threshold(threshold: number) { doc.threshold = String(threshold) },
  get staticCoefficients() { return doc.staticCoefficients.split(',').map(n => parseInt(n) || 0) },
  set staticCoefficients(coefficients: number[]) { doc.staticCoefficients = coefficients.join(',') },
  get coefficients() {
    return this.staticCoefficients
      .concat(Array(cont.threshold - 1).fill(0)).slice(0, cont.threshold - 1) // ensure the right size
      .map(c => doc.staticCoefficientsSelected ? c : random(0, 257)) // next line: ensure last coefficient is not 0
      .map((c, i) => (c != 0 || i != cont.threshold - 2) ? c : (doc.staticCoefficientsSelected ? 1 : random(1, 257)))
  },
  // recover secret from shares
  get availableShares() { return parseInt(doc.availableShares) },
  set availableShares(shares: number) { doc.availableShares = String(shares) },
}

// document automation
let aut = {
  // split secret into shares
  fillSecretNumbersFromText: () => 
    cont.secretNumbers = fromUTF8(doc.secretText),
  fixSecretNumbers: () => 
    cont.secretNumbers = cont.secretNumbers.map(n => limit(n, 0, 257)),
  fixNumerOfShares: () =>
    cont.numberOfShares = limit(cont.numberOfShares, cont.threshold, 257),
  fixThreshold: () =>
    cont.threshold = limit(cont.threshold, 2, cont.numberOfShares + 1),
  fixStaticCoefficients: () =>
    cont.staticCoefficients = cont.staticCoefficients.map(c => limit(c, 0, 257)),
  displayPolynomial: () =>
    span('polynomialSpan').innerHTML =
      foldLeft(cont.coefficients, "P(x) = geheimnis", (c, result, index) => `${result} + ${c}x<sup>${index + 1}</sup>`) + ' | (mod 257)',
  // recover secret from shares
  generateShareInputs: () => {
    doc.shareInputs = Array(256).fill('').map((_, index) => 
      `<p id="share${index}Paragraph">Teil ${index + 1}: <input id="share${index}}Input" type="text" size="45"></p>`
    ).join('')
  },
  handleAvailableShares: () => {
    cont.availableShares = limit(cont.availableShares, 2, 257)
    for (let i = 0; i < 256; i++)
      paragraph(`share${i}Paragraph`).hidden = i >= cont.availableShares
  },
}

const createShares = () => {
  let polynomial = foldLeft(cont.coefficients, "P(x) = geheimnis", (_, result, index) => `${result} + c${index + 1}*x^${index + 1}`) + ' | (mod 257)'
  let result: string[] = []
  let coefficients = cont.secretNumbers.map(_ => cont.coefficients)
  for (let x = 1; x <= cont.numberOfShares; x++) {
    let share = cont.secretNumbers.map((secret, index) => math.calculatePolynomial(x, [secret].concat(coefficients[index]), 257))
    result.push(`${polynomial}<br>P(${x})=${share.join(',')}`)
  }
  doc.sharesHtml = result.join('<br><br>')
}

const recoverSecret = () => {
  let providedParts = Array(cont.availableShares).fill('').map((_, index) => doc.shareInput(index))
  let rawParts = providedParts.map(part => {
    let regex1 = /P\((\d*)\)=(.*)/g
    let [_, xString, yStrings] = part.matchAll(regex1).next().value
    let x = parseInt(xString) || 1
    let ys = String(yStrings).split(',').map(s => parseInt(s) || 0)
    return {x, ys}
  })
  console.log(rawParts)
}

// wire the document functions: split secret into shares
aut.fillSecretNumbersFromText()
aut.displayPolynomial()

button('secretTextToNumbersButton').addEventListener('click', aut.fillSecretNumbersFromText)
input('secretNumbersInput').addEventListener('change', aut.fixSecretNumbers)
input('numberOfSharesInput').addEventListener('change', aut.fixNumerOfShares)
input('thresholdInput').addEventListener('change', aut.fixThreshold)
input('thresholdInput').addEventListener('change', aut.displayPolynomial)
radio('randomCoefficientsRadio').addEventListener('change', aut.displayPolynomial)
radio('staticCoefficientsRadio').addEventListener('change', aut.displayPolynomial)
input('staticCoefficientsInput').addEventListener('change', aut.fixStaticCoefficients)
input('staticCoefficientsInput').addEventListener('change', aut.displayPolynomial)
button('createSharesButton').addEventListener('click', createShares)

// wire the document functions: recover secret from shares
aut.generateShareInputs()
doc.setShareInput(0, 'P(1)=42,139,86,192,150,177,247,208,184')
doc.setShareInput(1, 'P(4)=59,165,20,137,256,194,12,170,101')
doc.setShareInput(2, 'P(2)=116,248,66,57,233,171,105,39,119')
aut.handleAvailableShares()
input('availableSharesInput').addEventListener('change', aut.handleAvailableShares)
button('recreateButton').addEventListener('click', recoverSecret)

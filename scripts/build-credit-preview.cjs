const fs = require('node:fs');
const path = require('node:path');
const root = path.resolve(__dirname, '..');
const data = (file, mime) => `data:${mime};base64,${fs.readFileSync(path.join(root, file)).toString('base64')}`;
const values = {
  FONTS: [
    ['SNDisplay', 600, 'SBSansDisplay-SemiBold.ttf'],
    ['SNText', 400, 'SBSansText-Regular.ttf'],
    ['SNText', 600, 'SBSansText-Semibold.ttf'],
  ].map(([family, weight, file]) => `@font-face{font-family:${family};font-weight:${weight};src:url('${data('assets/fonts/' + file, 'font/ttf')}') format('truetype');font-display:swap}`).join('\n'),
  ARROW: data('assets/icons/sc_srv_caretright_stroke_16.svg', 'image/svg+xml'),
  CLEAR: data('assets/icons/sc_srv_cross_stroke_16.svg', 'image/svg+xml'),
  SKELETON_AMOUNT: data('assets/skeleton/sn_skeleton_amount.svg', 'image/svg+xml'),
  SKELETON_TEXT: data('assets/skeleton/sn_credit_text.svg', 'image/svg+xml'),
  SKELETON_BUTTON: data('assets/skeleton/sn_credit_button.svg', 'image/svg+xml'),
};
let fragment = fs.readFileSync(path.join(root, 'preview/credit-potential.template.html'), 'utf8');
for (const [key, value] of Object.entries(values)) fragment = fragment.replaceAll(`__${key}__`, value);
if (/__[A-Z_]+__/.test(fragment)) throw new Error('Unresolved template variable');
if (Buffer.byteLength(fragment) >= 1e6) throw new Error('Preview exceeds 1 MB');
fs.writeFileSync(path.join(root, 'preview/credit-potential.fragment.html'), fragment);
if (process.argv[2]) fs.writeFileSync(process.argv[2], fragment);
console.log('Credit Potential preview:', Buffer.byteLength(fragment), 'bytes');

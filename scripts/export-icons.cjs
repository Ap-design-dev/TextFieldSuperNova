// Rasterize the original SVGs without changing their paths, colors or aspect ratio.
// Usage: npm install && npm run assets
const fs = require('node:fs/promises');
const path = require('node:path');
const sharp = require('sharp');
const root = path.resolve(__dirname, '..');

async function main() {
  const catalogue = path.join(root, 'ios/Sources/TextFieldSuperNova/Resources/Icons.xcassets');
  await fs.mkdir(catalogue, { recursive: true });
  await fs.writeFile(path.join(catalogue, 'Contents.json'), JSON.stringify({info:{author:'xcode', version:1}}, null, 2) + '\n');
  const sources = [
    ['icons','sc_srv_caretright_stroke_16'], ['icons','sc_srv_cross_stroke_16'],
    ['skeleton','sn_skeleton_label'], ['skeleton','sn_skeleton_amount'],
    ['skeleton','sn_skeleton_description'], ['skeleton','sn_skeleton_suggestion'],
  ];
  for (const [folder, name] of sources) {
    const source = path.join(root, 'assets', folder, name + '.svg');
    const metadata = await sharp(source).metadata();
    const width = metadata.width;
    const height = metadata.height;
    if (!width || !height) throw new Error('Missing SVG geometry: ' + name);
    const imageset = path.join(catalogue, name + '.imageset');
    await fs.mkdir(imageset, { recursive: true });
    const images = [];
    for (const scale of [1, 2, 3]) {
      const filename = `${name}@${scale}x.png`;
      await sharp(source, { density: 72 * scale }).resize(width * scale, height * scale).png().toFile(path.join(imageset, filename));
      images.push({filename, idiom:'universal', scale:`${scale}x`});
    }
    await fs.writeFile(path.join(imageset, 'Contents.json'), JSON.stringify({images, info:{author:'xcode',version:1}, properties:{'template-rendering-intent':'original'}}, null, 2) + '\n');
    for (const [qualifier, scale] of [['mdpi',1], ['hdpi',1.5], ['xhdpi',2], ['xxhdpi',3], ['xxxhdpi',4]]) {
      const folder = path.join(root, 'android/library/src/main/res', 'drawable-' + qualifier);
      await fs.mkdir(folder, { recursive:true });
      await sharp(source, { density:72 * scale }).resize(Math.round(width * scale), Math.round(height * scale)).png().toFile(path.join(folder, name + '.png'));
    }
    console.log(`${name}: ${width} × ${height}, iOS 1–3x, Android mdpi–xxxhdpi`);
  }
}
main().catch(error => { console.error(error); process.exitCode = 1; });

# Branding

Shared identity for the developer profile on Google Play (Play Console → Developer page).

| File | Use | Size |
|---|---|---|
| `developer-profile/developer-icon-512.png` | Developer icon | 512×512 |
| `developer-profile/developer-header-4096x2304.jpg` | Header image | 4096×2304 |

Promotional text (≤ 140 chars, no language tags):
- es: `Apps Android sencillas, útiles y sin anuncios. Hechas con cariño por un desarrollador independiente.`
- en: `Simple, useful Android apps with no ads. Built with care by an independent developer.`

Regenerate the images:
```bash
scripts/setup-python.sh   # once
.venv/bin/python branding/tools/generate_developer_profile.py
```
Upload them from a computer, not from iOS Photos (it resizes images and Play rejects the header).

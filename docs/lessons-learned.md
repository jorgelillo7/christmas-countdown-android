# Lessons learned

Short, one line each. Add to it whenever something bites.

- **Test the release build before uploading.** v4's toolchain upgrade crashed on start only in
  release (R8). `tools/smoke_test.sh` exists for this.
- **Verify the bundle you upload** with `scripts/verify-bundle.sh`: version, signature, permissions.
  A stale `.aab` from an earlier build is easy to grab by mistake.
- **Inactive developer accounts get closed.** Log in and ship something at least once a year.
- **Keep the keystore and its password backed up outside the repo.** Without them you need an
  upload key reset (days).
- **Files with a leading space** (` keystore.properties`) dodge `.gitignore`. Check `git status`
  before committing anything near secrets.
- **Don't host the privacy policy in a repo you may rename**: GitHub Pages URLs don't redirect.
- **Stacked PRs**: merging the base PR with `--delete-branch` closes the child PR. Retarget the
  child to `master` first.
- **Don't publish a store listing ahead of the release** that adds the features it describes.
- **The emulator is heavy on this Mac.** Run capture/test scripts in the foreground with Chrome
  closed; background jobs get killed under memory pressure.

call markdownlint -c markdown-lint.json --disable MD033 -- *.md
pushd docs-dev
cd
call markdownlint -c ..\markdown-lint.json --disable MD033 -- *.md
popd
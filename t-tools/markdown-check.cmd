@pushd ..
@echo =====================================================================
@echo case-A. currDir=%CD%
@call markdownlint -c t-tools\markdown-lint.json --disable MD033 -- *.md

@pushd t-docs-dev
@echo =====================================================================
@echo case-B. currDir=%CD%
@call markdownlint -c ..\t-tools\markdown-lint.json --disable MD033 -- *.md
@popd
@popd
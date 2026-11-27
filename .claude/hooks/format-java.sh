#!/usr/bin/env bash
# Hook Stop do Claude Code: ao fim de cada resposta, formata o codigo com Spotless,
# mas so quando existe arquivo .java alterado e ainda nao commitado.
# Sempre sai com 0 para nunca bloquear o Claude.

cd "${CLAUDE_PROJECT_DIR:-.}" || exit 0

if ! git status --porcelain -- '*.java' 2>/dev/null | grep -q .; then
  exit 0
fi

if ! sh ./mvnw -q spotless:apply >/dev/null 2>&1; then
  echo "spotless:apply falhou - rode ./mvnw spotless:apply para ver o erro" >&2
fi

exit 0

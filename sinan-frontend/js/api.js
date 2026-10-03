// Endereço da API Spring Boot. Ajuste se ela rodar em outra porta ou servidor.
const API_URL = 'http://localhost:8080/notificacao';

class ApiError extends Error {
  constructor(status, mensagem, erros = []) {
    super(mensagem);
    this.status = status;
    this.erros = erros; // [{campo, mensagem}] quando a API devolve 400
  }
}

async function chamar(caminho, opcoes = {}) {
  let resposta;
  try {
    resposta = await fetch(API_URL + caminho, {
      ...opcoes,
      headers: opcoes.body ? { 'Content-Type': 'application/json' } : undefined,
    });
  } catch {
    throw new ApiError(0, 'Não foi possível acessar a API em ' + API_URL + '. Confira se ela está rodando.');
  }
  if (resposta.status === 204) return null;
  const corpo = await resposta.json().catch(() => null);
  if (!resposta.ok) {
    throw new ApiError(resposta.status, (corpo && corpo.detail) || 'Erro ' + resposta.status, (corpo && corpo.erros) || []);
  }
  return corpo;
}

const Api = {
  listar: (params) => chamar('?' + params.toString()),
  buscar: (id) => chamar('/' + id),
  criar: (dados) => chamar('', { method: 'POST', body: JSON.stringify(dados) }),
  atualizar: (id, dados) => chamar('/' + id, { method: 'PUT', body: JSON.stringify(dados) }),
  excluir: (id) => chamar('/' + id, { method: 'DELETE' }),
};

function formatarData(iso) {
  if (!iso) return '';
  const [a, m, d] = iso.split('-');
  return d + '/' + m + '/' + a;
}

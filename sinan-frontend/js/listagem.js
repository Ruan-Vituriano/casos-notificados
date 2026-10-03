(function () {
  const COLUNAS = [
    { chave: 'id', rotulo: 'ID' },
    { chave: 'agravo', rotulo: 'Agravo' },
    { chave: 'nomePaciente', rotulo: 'Paciente' },
    { chave: 'municipioNotificacao', rotulo: 'Município' },
    { chave: 'dataNotificacao', rotulo: 'Data da notificação' },
  ];
  const estado = { pagina: 1, ordenarPor: 'dataNotificacao', ordem: 'DESC', total: 0, totalPaginas: 0 };
  const $ = (id) => document.getElementById(id);
  let alvoExclusao = null;

  function avisar(texto, tipo) {
    const a = $('aviso');
    a.hidden = !texto;
    a.className = 'aviso ' + (tipo || 'ok');
    a.textContent = texto || '';
  }

  function cabecalho() {
    const tr = $('cabecalho');
    tr.replaceChildren();
    for (const c of COLUNAS) {
      const th = document.createElement('th');
      th.scope = 'col';
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.append(c.rotulo + ' ');
      const seta = document.createElement('span');
      seta.className = 'seta';
      seta.textContent = '↕';
      if (estado.ordenarPor === c.chave) {
        th.setAttribute('aria-sort', estado.ordem === 'ASC' ? 'ascending' : 'descending');
        seta.textContent = estado.ordem === 'ASC' ? '▲' : '▼';
      }
      btn.append(seta);
      btn.addEventListener('click', () => {
        estado.ordem = estado.ordenarPor === c.chave && estado.ordem === 'ASC' ? 'DESC' : 'ASC';
        estado.ordenarPor = c.chave;
        estado.pagina = 1;
        carregar();
      });
      th.append(btn);
      tr.append(th);
    }
    const acoes = document.createElement('th');
    acoes.scope = 'col';
    acoes.className = 'fim';
    acoes.textContent = 'Ações';
    tr.append(acoes);
  }

  function parametros() {
    const p = new URLSearchParams();
    for (const [k, v] of new FormData($('filtros'))) if (v.trim()) p.set(k, v.trim());
    p.set('pagina', estado.pagina);
    p.set('ordenarPor', estado.ordenarPor);
    p.set('ordem', estado.ordem);
    return p;
  }

  function botao(texto, classe, aoClicar) {
    const b = document.createElement('button');
    b.type = 'button';
    b.textContent = texto;
    b.className = classe;
    b.addEventListener('click', aoClicar);
    return b;
  }

  function linha(n, duplicadas) {
    const tr = document.createElement('tr');
    if (duplicadas) tr.className = 'dup';
    const valores = [n.id, n.agravo, n.nomePaciente, n.municipioNotificacao, formatarData(n.dataNotificacao)];
    valores.forEach((v, i) => {
      const td = document.createElement('td');
      td.textContent = v ?? '';
      if (i === 1 && duplicadas) {
        const selo = document.createElement('span');
        selo.className = 'selo';
        selo.textContent = 'duplicada';
        td.append(selo);
      }
      tr.append(td);
    });
    const fim = document.createElement('td');
    fim.className = 'fim';
    fim.append(
      botao('Alterar', 'secundario', () => { location.href = 'cadastro.html?id=' + n.id; }),
      botao('Excluir', 'perigo', () => pedirExclusao(n))
    );
    tr.append(fim);
    return tr;
  }

  async function carregar() {
    cabecalho();
    try {
      const p = await Api.listar(parametros());
      if (!p.conteudo.length && p.total > 0 && estado.pagina > 1) {
        estado.pagina = p.totalPaginas; // a última página esvaziou (ex.: após excluir)
        return carregar();
      }
      const duplicadas = $('duplicadas').checked;
      $('corpo').replaceChildren(...p.conteudo.map((n) => linha(n, duplicadas)));
      $('vazio').hidden = p.conteudo.length > 0;
      estado.total = p.total;
      estado.totalPaginas = p.totalPaginas;
      $('resumo').textContent = p.total
        ? p.total + ' notificações · página ' + p.pagina + ' de ' + p.totalPaginas
        : '0 notificações';
      $('anterior').disabled = p.pagina <= 1;
      $('proxima').disabled = p.pagina >= p.totalPaginas;
      if ($('aviso').classList.contains('erro-geral')) avisar('');
    } catch (e) {
      $('corpo').replaceChildren();
      $('vazio').hidden = true;
      $('resumo').textContent = '';
      avisar(e.message, 'erro-geral');
    }
  }

  function pedirExclusao(n) {
    alvoExclusao = n;
    $('confirmacaoTexto').textContent = 'Excluir a notificação #' + n.id + ' de ' + n.nomePaciente + '? Essa ação não pode ser desfeita.';
    $('confirmacao').showModal();
  }

  $('confirmar').addEventListener('click', async () => {
    const n = alvoExclusao;
    $('confirmacao').close();
    try {
      await Api.excluir(n.id);
      avisar('Notificação #' + n.id + ' excluída.');
      carregar();
    } catch (e) {
      avisar(e.message, 'erro-geral');
    }
  });
  $('cancelar').addEventListener('click', () => $('confirmacao').close());

  $('filtros').addEventListener('submit', (ev) => { ev.preventDefault(); estado.pagina = 1; carregar(); });
  $('limpar').addEventListener('click', () => { $('filtros').reset(); estado.pagina = 1; carregar(); });
  $('anterior').addEventListener('click', () => { estado.pagina--; carregar(); });
  $('proxima').addEventListener('click', () => { estado.pagina++; carregar(); });

  if (new URLSearchParams(location.search).has('salvo')) {
    avisar('Notificação salva.');
    history.replaceState(null, '', 'index.html');
  }
  carregar();
})();

(function () {
  const $ = (id) => document.getElementById(id);
  const id = new URLSearchParams(location.search).get('id');
  const hoje = new Date().toISOString().slice(0, 10);

  const SIM_NAO = [['1', 'Sim'], ['2', 'Não'], ['9', 'Ignorado']];
  const t = (k, rotulo, extra) => ({ k, rotulo, ...extra });
  const data = (k, rotulo, extra) => t(k, rotulo, { tipo: 'date', max: hoje, ...extra });
  const uf = (k, rotulo, extra) => t(k, rotulo, { maxlength: 2, maiusculo: true, ...extra });
  const cod = (k, rotulo, extra) => t(k, rotulo, { tipo: 'number', min: 0, ...extra });

  // Cada seção vira um fieldset (ou <details> quando recolhível). Chaves "residencia.x" vão no objeto aninhado.
  const SECOES = [
    { titulo: 'Dados gerais', campos: [
      t('agravo', 'Agravo / doença', { req: 1 }), t('codigoCid10', 'CID-10'),
      data('dataNotificacao', 'Data da notificação', { req: 1 }), uf('ufNotificacao', 'UF da notificação', { req: 1 }),
      t('municipioNotificacao', 'Município da notificação'), t('codigoIbgeNotificacao', 'Código IBGE'),
      t('unidadeSaude', 'Unidade de saúde notificadora', { req: 1 }), t('codigoUnidadeSaude', 'Código da unidade'),
      data('dataPrimeirosSintomas', 'Data dos primeiros sintomas', { req: 1 }) ] },
    { titulo: 'Paciente', campos: [
      t('nomePaciente', 'Nome do paciente', { req: 1 }), data('dataNascimento', 'Data de nascimento'),
      cod('idade', 'Idade (se não souber o nascimento)'),
      t('unidadeIdade', 'Unidade da idade', { num: 1, opcoes: [['1', 'Hora'], ['2', 'Dia'], ['3', 'Mês'], ['4', 'Ano']] }),
      t('sexo', 'Sexo', { req: 1, opcoes: [['M', 'Masculino'], ['F', 'Feminino'], ['I', 'Ignorado']] }),
      t('gestante', 'Gestante', { num: 1, opcoes: [['1', '1º trimestre'], ['2', '2º trimestre'], ['3', '3º trimestre'], ['4', 'Idade gestacional ignorada'], ['5', 'Não'], ['6', 'Não se aplica'], ['9', 'Ignorado']] }),
      t('racaCor', 'Raça/cor', { num: 1, opcoes: [['1', 'Branca'], ['2', 'Preta'], ['3', 'Amarela'], ['4', 'Parda'], ['5', 'Indígena'], ['9', 'Ignorado']] }),
      cod('escolaridade', 'Escolaridade (código SINAN)'),
      t('cartaoSus', 'Cartão SUS', { maxlength: 15, pattern: '\\d{15}', dica: '15 dígitos' }), t('nomeMae', 'Nome da mãe') ] },
    { titulo: 'Residência', campos: [
      uf('residencia.uf', 'UF'), t('residencia.municipio', 'Município'), t('residencia.codigoIbge', 'Código IBGE'),
      t('residencia.distrito', 'Distrito'), t('residencia.bairro', 'Bairro'), t('residencia.logradouro', 'Logradouro'),
      t('residencia.codigoLogradouro', 'Código do logradouro'), t('residencia.numero', 'Número'), t('residencia.complemento', 'Complemento'),
      t('residencia.geoCampo1', 'Geo campo 1'), t('residencia.geoCampo2', 'Geo campo 2'), t('residencia.pontoReferencia', 'Ponto de referência'),
      t('residencia.cep', 'CEP', { pattern: '\\d{5}-?\\d{3}', dica: '00000-000' }), t('residencia.telefone', 'Telefone', { tipo: 'tel' }),
      t('residencia.zona', 'Zona', { num: 1, opcoes: [['1', 'Urbana'], ['2', 'Rural'], ['3', 'Periurbana'], ['9', 'Ignorado']] }),
      t('residencia.pais', 'País (se residir no exterior)') ],
      dica: 'Informe a UF e o município; sem UF, o país é obrigatório.' },
    { titulo: 'Conclusão do caso', recolhida: true, campos: [
      data('dataInvestigacao', 'Data da investigação'), cod('classificacaoFinal', 'Classificação final (código)'),
      cod('criterioConfirmacao', 'Critério de confirmação (código)'),
      t('casoAutoctone', 'Caso autóctone', { num: 1, opcoes: [['1', 'Sim'], ['2', 'Não'], ['3', 'Indeterminado']] }),
      uf('ufInfeccao', 'UF da infecção'), t('paisInfeccao', 'País da infecção'), t('municipioInfeccao', 'Município da infecção'),
      t('codigoIbgeMunicipioInfeccao', 'Código IBGE (infecção)'), t('distritoInfeccao', 'Distrito da infecção'), t('bairroInfeccao', 'Bairro da infecção'),
      t('doencaTrabalho', 'Doença relacionada ao trabalho', { num: 1, opcoes: SIM_NAO }), cod('evolucaoCaso', 'Evolução do caso (código)'),
      data('dataObito', 'Data do óbito'), data('dataEncerramento', 'Data de encerramento'),
      t('observacoes', 'Observações', { tipo: 'textarea' }) ] },
    { titulo: 'Investigador', recolhida: true, campos: [
      t('municipioUnidadeInvestigador', 'Município da unidade'), t('codigoUnidadeInvestigador', 'Código da unidade'),
      t('nomeInvestigador', 'Nome do investigador'), t('funcaoInvestigador', 'Função') ] },
  ];
  const CAMPOS = SECOES.flatMap((s) => s.campos);

  // Regras da API que aparecem com o nome do método de validação, não do campo.
  const ALIAS = { idadeObrigatoriaSemNascimento: 'idade', unidadeIdadeObrigatoriaComIdade: 'unidadeIdade', gestanteObrigatorioParaSexoFeminino: 'gestante' };

  function criar(f) {
    const div = document.createElement('div');
    div.className = 'campo';
    const label = document.createElement('label');
    label.htmlFor = 'f-' + f.k;
    label.textContent = f.rotulo + (f.req ? ' *' : '');
    let input;
    if (f.opcoes) {
      input = document.createElement('select');
      input.append(new Option('Selecione', ''));
      f.opcoes.forEach(([v, texto]) => input.append(new Option(texto, v)));
    } else if (f.tipo === 'textarea') {
      input = document.createElement('textarea');
    } else {
      input = document.createElement('input');
      input.type = f.tipo || 'text';
      if (f.min !== undefined) input.min = f.min;
      if (f.max) input.max = f.max;
      if (f.maxlength) input.maxLength = f.maxlength;
      if (f.pattern) input.pattern = f.pattern;
      if (f.dica) input.placeholder = f.dica;
    }
    input.id = 'f-' + f.k;
    input.name = f.k;
    input.required = !!f.req;
    const erro = document.createElement('small');
    erro.className = 'erro';
    erro.id = 'e-' + f.k;
    input.setAttribute('aria-describedby', erro.id);
    div.append(label, input, erro);
    return div;
  }

  function montar() {
    for (const s of SECOES) {
      const raiz = document.createElement(s.recolhida ? 'details' : 'fieldset');
      const cab = document.createElement(s.recolhida ? 'summary' : 'legend');
      cab.textContent = s.titulo;
      raiz.append(cab);
      if (s.recolhida) raiz.className = 'painel';
      if (s.dica) { const p = document.createElement('p'); p.textContent = s.dica; raiz.append(p); }
      const grade = document.createElement('div');
      grade.className = 'grade';
      s.campos.forEach((f) => grade.append(criar(f)));
      raiz.append(grade);
      $('secoes').append(raiz);
    }
  }

  const el = (k) => $('f-' + k);
  const lerAninhado = (obj, k) => k.split('.').reduce((o, p) => (o == null ? o : o[p]), obj);

  function preencher(d) {
    for (const f of CAMPOS) {
      const v = lerAninhado(d, f.k);
      el(f.k).value = v == null ? '' : v;
    }
    document.querySelectorAll('details').forEach((det) => {
      if ([...det.querySelectorAll('input,select,textarea')].some((c) => c.value)) det.open = true;
    });
  }

  function coletar() {
    const dados = { residencia: {} };
    for (const f of CAMPOS) {
      let v = el(f.k).value.trim();
      if (v === '') v = null;
      else if (f.tipo === 'number' || f.num) v = Number(v);
      else if (f.maiusculo) v = v.toUpperCase();
      if (f.k.startsWith('residencia.')) dados.residencia[f.k.slice(11)] = v;
      else dados[f.k] = v;
    }
    return dados;
  }

  function limparErros() {
    document.querySelectorAll('.erro').forEach((e) => { e.textContent = ''; });
    document.querySelectorAll('[aria-invalid]').forEach((e) => e.removeAttribute('aria-invalid'));
    $('aviso').hidden = true;
  }

  function mostrarErros(titulo, mensagens) {
    const a = $('aviso');
    a.replaceChildren(titulo);
    if (mensagens.length) {
      const ul = document.createElement('ul');
      mensagens.forEach((m) => { const li = document.createElement('li'); li.textContent = m; ul.append(li); });
      a.append(ul);
    }
    a.hidden = false;
    a.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  function marcar(chave, mensagem) {
    const alvo = $('e-' + chave);
    if (alvo) { alvo.textContent = mensagem; el(chave).setAttribute('aria-invalid', 'true'); }
    const det = alvo && alvo.closest('details');
    if (det) det.open = true;
  }

  $('formulario').addEventListener('submit', async (ev) => {
    ev.preventDefault();
    limparErros();
    const form = ev.target;
    const invalidos = [...form.elements].filter((e) => e.willValidate && !e.checkValidity());
    invalidos.forEach((e) => { const d = e.closest('details'); if (d) d.open = true; });
    if (invalidos.length) { form.reportValidity(); return; }

    $('salvar').disabled = true;
    try {
      const dados = coletar();
      if (id) await Api.atualizar(id, dados); else await Api.criar(dados);
      location.href = 'index.html?salvo=1';
    } catch (e) {
      $('salvar').disabled = false;
      if (e.status === 400 && e.erros.length) {
        e.erros.forEach((x) => marcar(ALIAS[x.campo] || x.campo, x.mensagem));
        mostrarErros('Corrija os campos abaixo:', e.erros.map((x) => x.mensagem));
      } else {
        mostrarErros(e.message, []);
      }
    }
  });

  montar();
  if (id) {
    $('titulo').textContent = 'Atualizar notificação #' + id;
    $('salvar').textContent = 'Salvar alterações';
    Api.buscar(id).then(preencher).catch((e) => {
      $('salvar').disabled = true;
      mostrarErros(e.status === 404 ? 'Notificação #' + id + ' não encontrada. Ela pode ter sido excluída.' : e.message, []);
    });
  }
})();

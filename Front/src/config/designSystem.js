export const PALETA = [
  { nome: "Destaque", variavel: "--amarelo", hex: "#efc45b", uso: "Ação de destaque no início." },
  {
    nome: "Texto",
    variavel: "--texto",
    hex: "#26332d",
    uso: "Texto principal e campos.",
  },
  {
    nome: "Fundo",
    variavel: "--fundo",
    hex: "#f6f4ed",
    uso: "Fundo das páginas.",
  },
  {
    nome: "Superfície",
    variavel: "--superficie",
    hex: "#ffffff",
    uso: "Formulários, cartões e painéis.",
  },
  {
    nome: "Verde",
    variavel: "--verde",
    hex: "#285d42",
    uso: "Botão principal e abas selecionadas.",
  },
  {
    nome: "Verde escuro",
    variavel: "--verde-escuro",
    hex: "#1d4932",
    uso: "Cabeçalho, rodapé e botão principal ao passar o mouse.",
  },
  {
    nome: "Link",
    variavel: "--link",
    hex: "#245b40",
    uso: "Links e nome da aplicação.",
  },
  {
    nome: "Texto de apoio",
    variavel: "--texto-apoio",
    hex: "#56645c",
    uso: "Descrições, datas e rodapé.",
  },
  {
    nome: "Borda",
    variavel: "--borda",
    hex: "#d5dcd7",
    uso: "Painéis e linhas de tabela.",
  },
  {
    nome: "Borda do campo",
    variavel: "--borda-campo",
    hex: "#9ba69f",
    uso: "Campos, botões comuns e modal.",
  },
  {
    nome: "Hover neutro",
    variavel: "--hover-neutro",
    hex: "#e9eeeb",
    uso: "Botões comuns ao passar o mouse.",
  },
  {
    nome: "Seleção",
    variavel: "--selecao",
    hex: "#e5eee8",
    uso: "Página ativa no menu.",
  },
  {
    nome: "Cabeçalho de tabela",
    variavel: "--cabecalho-tabela",
    hex: "#eff2f0",
    uso: "Cabeçalho das tabelas.",
  },
  {
    nome: "Detalhe",
    variavel: "--fundo-detalhe",
    hex: "#f5f7f5",
    uso: "Parecer, crédito e orçamento.",
  },
  {
    nome: "Foco",
    variavel: "--foco",
    hex: "#477bba",
    uso: "Contorno de navegação por teclado.",
  },
  {
    nome: "Ação de risco",
    variavel: "--acao-perigo",
    hex: "#a12a25",
    uso: "Cancelar e excluir.",
  },
];

export const ESTADOS = [
  {
    nome: "Neutro",
    variavel: "--neutro-fundo",
    hex: "#f0f2f1",
    variavelTexto: "--texto",
    texto: "#26332d",
  },
  {
    nome: "Sucesso",
    variavel: "--sucesso-fundo",
    hex: "#e5f2e8",
    variavelTexto: "--sucesso-texto",
    texto: "#205c33",
  },
  {
    nome: "Erro",
    variavel: "--erro-fundo",
    hex: "#faeceb",
    variavelTexto: "--erro-texto",
    texto: "#8e2924",
  },
];

export const FONTES = [
  {
    nome: "Arial",
    variavel: "--fonte-texto",
    familia: "Arial, Helvetica, sans-serif",
    uso: "Títulos, textos, tabelas e formulários.",
  },
];

export const ESCALA_TIPO = [
  {
    nome: "Título de página",
    classe: "h1",
    variavel: "--titulo-pagina",
    tamanho: "32px",
    peso: 700,
    exemplo: "Resumo dos pedidos",
  },
  {
    nome: "Título de seção",
    classe: "h2",
    variavel: "--titulo-secao",
    tamanho: "20px",
    peso: 700,
    exemplo: "Dados do contratante",
  },
  {
    nome: "Texto",
    classe: "body",
    variavel: "--tamanho-texto",
    tamanho: "16px",
    peso: 400,
    exemplo: "Consulte a frota e acompanhe seus pedidos.",
  },
  {
    nome: "Texto de apoio",
    classe: ".small-text",
    variavel: "--texto-pequeno",
    tamanho: "14px",
    peso: 400,
    exemplo: "Aguarde a análise financeira do pedido.",
  },
];

export const RAIOS = [
  {
    nome: "Campo e painel",
    variavel: "--raio-campo",
    valor: "4px",
    uso: "Botões, formulários, painéis e status.",
  },
];

export const MEDIDAS = [
  {
    nome: "Largura do conteúdo",
    variavel: "--largura-conteudo",
    valor: "1180px",
  },
  {
    nome: "Margem lateral do conteúdo",
    variavel: ".content",
    valor: "20px; 16px no celular",
  },
  {
    nome: "Quebra para celular",
    variavel: "@media",
    valor: "650px",
  },
  {
    nome: "Espaço entre campos",
    variavel: ".form",
    valor: "16px",
  },
];

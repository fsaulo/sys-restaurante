CREATE TABLE IF NOT EXISTS "caixa" (
	"id_caixa"	INTEGER NOT NULL UNIQUE,
	"id_usuario"	INTEGER,
	"data_abertura"	DATE,
	"data_fechamento"	DATE,
	"hora_abertura"	TIME,
	"hora_fechamento"	TIME,
	"observacao"	VARCHAR,
	"is_aberto"	BOOLEAN,
	"balanco"	DOUBLE,
	"balanco_inicial"	DOUBLE,
	"total_retiradas"	DOUBLE,
	"total_avista"	DOUBLE,
	"total_acartao"	DOUBLE,
	PRIMARY KEY("id_caixa" AUTOINCREMENT),
	FOREIGN KEY("id_usuario") REFERENCES "usuario"("id_usuario")
);
CREATE TABLE IF NOT EXISTS "cardapio" (
	"id_produto"	INTEGER,
	"is_prato_do_dia"	BOOLEAN,
	FOREIGN KEY("id_produto") REFERENCES "produto"("id_produto")
);
CREATE TABLE IF NOT EXISTS "categoria_mesa" (
	"id_categoria_mesa"	INTEGER NOT NULL UNIQUE,
	"descricao"	VARCHAR,
	PRIMARY KEY("id_categoria_mesa" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "categoria_pedido" (
	"id_categoria_pedido"	INTEGER,
	"descricao"	VARCHAR,
	PRIMARY KEY("id_categoria_pedido" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "categoria_produto" (
	"id_categoria_produto"	INTEGER NOT NULL UNIQUE,
	"descricao"	VARCHAR,
	PRIMARY KEY("id_categoria_produto" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "comanda" (
	"id_comanda"	INTEGER NOT NULL UNIQUE,
	"id_pedido"	INTEGER,
	"id_mesa"	INTEGER,
	"id_caixa"	INTEGER,
	"id_funcionario"	INTEGER,
	"id_categoria_pedido"	INTEGER,
	"is_aberto"	BOOLEAN,
	"data_abertura"	DATE,
	"data_fechamento"	DATE,
	"hora_abertura"	TIME,
	"hora_fechamento"	TIME,
	"total"	DOUBLE,
	PRIMARY KEY("id_comanda" AUTOINCREMENT),
	FOREIGN KEY("id_pedido") REFERENCES "pedido"("id_pedido"),
	FOREIGN KEY("id_mesa") REFERENCES "mesa"("id_mesa"),
	FOREIGN KEY("id_caixa") REFERENCES "caixa"("id_caixa"),
	FOREIGN KEY("id_funcionario") REFERENCES "funcionario",
	FOREIGN KEY("id_categoria_pedido") REFERENCES "categoria_pedido"("id_categoria_pedido")
);
CREATE TABLE IF NOT EXISTS "comanda_has_produtos" (
	"id_comanda"	,
	"id_produto"	,
	FOREIGN KEY("id_comanda") REFERENCES "comanda",
	FOREIGN KEY("id_produto") REFERENCES "produto"("id_produto")
);
CREATE TABLE IF NOT EXISTS "funcionario" (
	"id_funcionario"	INTEGER NOT NULL UNIQUE,
	"nome"	VARCHAR NOT NULL,
	"salario"	DOUBLE,
	"dia_salario"	DATE,
	PRIMARY KEY("id_funcionario" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS lembrete (id_lembrete INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, id_usuario REFERENCES usuario (id_usuario) NOT NULL, conteudo VARCHAR, data DATE, is_marcado BOOLEAN);
CREATE TABLE IF NOT EXISTS "mesa" (
	"id_mesa"	INTEGER NOT NULL UNIQUE,
	"id_categoria_mesa"	INTEGER,
	PRIMARY KEY("id_mesa"),
	FOREIGN KEY("id_categoria_mesa") REFERENCES "categoria_mesa"("id_categoria_mesa")
);
CREATE TABLE IF NOT EXISTS "metadata" (
	"id_metadata"	INTEGER NOT NULL,
	"business_name"	VARCHAR,
	"cnpj"	VARCHAR,
	"address"	VARCHAR,
	"phone"	VARCHAR,
	PRIMARY KEY("id_metadata" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "pedido" (
	"id_pedido"	INTEGER NOT NULL UNIQUE,
	"id_usuario"	INTEGER,
	"id_caixa"	INTEGER,
	"id_categoria_pedido"	INTEGER,
	"status"	INTEGER,
	"data_pedido"	DATE,
	"hora_pedido"	TIME,
	"observacao"	VARCHAR,
	"valor_cartao"	DOUBLE,
	"valor_avista"	DOUBLE,
	"descontos"	DOUBLE,
	"taxas"	DOUBLE,
	"nome_cliente"	VARCHAR,
	PRIMARY KEY("id_pedido" AUTOINCREMENT),
	FOREIGN KEY("id_usuario") REFERENCES "usuario"("id_usuario"),
	FOREIGN KEY("id_caixa") REFERENCES "caixa"("id_caixa"),
	FOREIGN KEY("id_categoria_pedido") REFERENCES "categoria_pedido"("id_categoria_pedido")
);
CREATE TABLE IF NOT EXISTS "pedido_cozinha" (
	"id_pedido_cozinha"	INTEGER NOT NULL UNIQUE,
	"id_comanda"	INTEGER NOT NULL,
	"status"	INTEGER,
	"observacoes"	TEXT,
	"data_pedido"	DATETIME,
	"data_conclusao"	DATETIME,
	PRIMARY KEY("id_pedido_cozinha" AUTOINCREMENT),
	FOREIGN KEY("id_comanda") REFERENCES "comanda"("id_comanda")
);
CREATE TABLE IF NOT EXISTS "pedido_cozinha_has_produtos" (
	"id_pedido_cozinha"	INTEGER,
	"id_produto"	INTEGER,
	"qtd_produto"	INTEGER,
	FOREIGN KEY("id_pedido_cozinha") REFERENCES "pedido_cozinha"("id_pedido_cozinha"),
	FOREIGN KEY("id_produto") REFERENCES "produto"("id_produto")
);
CREATE TABLE IF NOT EXISTS "pedido_has_produtos" (
	"id_pedido"	INTEGER,
	"id_produto"	INTEGER,
	"qtd_pedido"	INTEGER,
	FOREIGN KEY("id_pedido") REFERENCES "pedido"("id_pedido"),
	FOREIGN KEY("id_produto") REFERENCES "produto"("id_produto") ON DELETE SET NULL
);
CREATE TABLE IF NOT EXISTS "produto" (
	"id_produto"	INTEGER NOT NULL UNIQUE,
	"id_categoria"	INTEGER,
	"qtd_estoque"	INTEGER,
	"qtd_vendidos"	INTEGER DEFAULT (0),
	"qtd_estoque_minimo"	INTEGER,
	"descricao"	VARCHAR,
	"preco_venda"	DOUBLE,
	"preco_varejo"	DOUBLE,
	"check_estoque"	BOOLEAN,
	"is_cardapio"	BOOLEAN,
	"is_ingrediente"	BOOLEAN,
	PRIMARY KEY("id_produto" AUTOINCREMENT),
	FOREIGN KEY("id_categoria") REFERENCES "categoria_produto"("id_categoria_produto") ON DELETE NO ACTION
);
CREATE TABLE IF NOT EXISTS sessao (id_sessao INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, id_usuario INTEGER REFERENCES usuario NOT NULL, data_sessao DATETIME, tempo_sessao TIME, duracao_sessao INT);
CREATE TABLE IF NOT EXISTS "usuario" (
	"id_usuario"	INTEGER NOT NULL UNIQUE,
	"id_funcionario"	INTEGER,
	"nome"	VARCHAR(20),
	"senha"	VARCHAR(20) NOT NULL,
	"username"	VARCHAR(20) NOT NULL UNIQUE,
	"email"	VARCHAR(20) UNIQUE,
	"is_admin"	BOOLEAN NOT NULL,
	PRIMARY KEY("id_usuario" AUTOINCREMENT),
	FOREIGN KEY("id_funcionario") REFERENCES "funcionario"("id_funcionario")
);

INSERT INTO usuario (nome, senha, username, email, is_admin) VALUES ('admin', 'vyVVzr278QsIiC2rdEAhVw==', 'admin', 'admin@sys-restaurante.com', '1');
INSERT INTO caixa (id_caixa, id_usuario, data_abertura, hora_abertura, balanco, balanco_inicial) VALUES ('1', '1', '1579402800000', '94526000', '0', '0');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('1', 'Pedido no caixa');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('2', 'Pedido em comanda');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('3', 'Pedido avariado');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('4', 'Retirada');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('5', 'Doação');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('6', 'Concluído');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('7', 'Aguardando pagamento');
INSERT INTO categoria_pedido (id_categoria_pedido, descricao) VALUES ('8', 'Aguardando preparo');
INSERT INTO categoria_produto (id_categoria_produto, descricao) VALUES ('1', 'Bebida');
INSERT INTO categoria_produto (id_categoria_produto, descricao) VALUES ('2', 'Almoço');
INSERT INTO categoria_produto (id_categoria_produto, descricao) VALUES ('3', 'Tira-gosto');
INSERT INTO categoria_produto (id_categoria_produto, descricao) VALUES ('4', 'Porção extra');
INSERT INTO categoria_produto (id_categoria_produto, descricao) VALUES ('5', 'Sem categoria');
-- public.stats definition

-- Drop table
DROP TABLE IF EXISTS public.stats;

-- Create table
CREATE TABLE public.stats (
	adnid int8 NOT NULL GENERATED ALWAYS AS IDENTITY, -- Identificador de Tabla
	adnkey varchar NOT NULL, -- Llave de ADN (SHA-1)
	adnsequence varchar NOT NULL, -- Sequencia de ADN
	ishuman bool NOT NULL, -- Describe si la sequencia es de un Humano
	ismutant bool NOT NULL, -- Describe si la sequencia es de un Mutante
	conteo int8 NOT NULL, -- Numero de veces que se ha ejecutado el analisis para la secuencia
	CONSTRAINT stats_pk PRIMARY KEY (adnid),
	CONSTRAINT stats_un_adnkey UNIQUE (adnkey)
);
CREATE INDEX stats_adnid_idx ON public.stats USING btree (adnid);

-- Column comments
COMMENT ON COLUMN public.stats.adnid IS 'Identificador de Tabla';
COMMENT ON COLUMN public.stats.adnkey IS 'Llave de ADN (SHA-256)';
COMMENT ON COLUMN public.stats.adnsequence IS 'Sequencia de ADN';
COMMENT ON COLUMN public.stats.ishuman IS 'Describe si la sequencia es de un Humano';
COMMENT ON COLUMN public.stats.ismutant IS 'Describe si la sequencia es de un Mutante';
COMMENT ON COLUMN public.stats.conteo IS 'Numero de veces que se ha ejecutado el analisis para la secuencia';

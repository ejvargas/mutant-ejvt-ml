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
	CONSTRAINT stats_pk PRIMARY KEY (adnid)
);
CREATE INDEX stats_adnid_idx ON public.stats USING btree (adnid);

-- Column comments
COMMENT ON COLUMN public.stats.adnid IS 'Identificador de Tabla';
COMMENT ON COLUMN public.stats.adnkey IS 'Llave de ADN (SHA-1)';
COMMENT ON COLUMN public.stats.adnsequence IS 'Sequencia de ADN';
COMMENT ON COLUMN public.stats.ishuman IS 'Describe si la sequencia es de un Humano';
COMMENT ON COLUMN public.stats.ismutant IS 'Describe si la sequencia es de un Mutante';

-- Permissions
ALTER TABLE public.stats OWNER TO [PUT_YOUR_USER_HERE];
GRANT ALL ON TABLE public.stats TO [PUT_YOUR_USER_HERE];

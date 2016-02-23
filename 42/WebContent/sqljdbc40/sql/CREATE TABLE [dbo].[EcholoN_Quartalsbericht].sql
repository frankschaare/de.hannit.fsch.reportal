USE [KLR]
GO

/****** Object:  Table [dbo].[EcholoN_Quartalsbericht]    Script Date: 16.05.2015 09:36:45 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[EcholoN_Quartalsbericht](
	[IncidentId] [uniqueidentifier] NOT NULL,
	[IncidentCreatedOn] [datetime] NOT NULL,
	[Vorgangsnummer] [nvarchar](60) NOT NULL,
	[Status] [nvarchar](60) NULL,
	[Typ] [nvarchar](60) NOT NULL,
	[Kategorie] [nvarchar](60) NULL,
	[Priorität] [nvarchar](60) NOT NULL,
	[OrganizationName] [nvarchar](60) NULL,
	[Reaktionszeit_eingehalten] [varchar](31) NOT NULL,
	[Zielzeit_eingehalten] [nvarchar](26) NOT NULL,
	[LösungszeitMinuten] [float] NULL
) ON [PRIMARY]

GO



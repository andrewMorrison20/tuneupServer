SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[address](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[address_line1] [varchar](255) NULL,
	[address_line2] [varchar](255) NULL,
	[city] [varchar](255) NULL,
	[country] [varchar](255) NULL,
	[latitude] [float] NULL,
	[longitude] [float] NULL,
	[postcode] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[app_user](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[email] [varchar](255) NULL,
	[name] [varchar](255) NULL,
	[password] [varchar](255) NULL,
	[username] [varchar](255) NULL,
	[address_id] [bigint] NULL,
	[is_verified] [bit] NULL,
	[deleted_at] [datetime2](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[availability](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[end_time] [datetime2](6) NULL,
	[start_time] [datetime2](6) NULL,
	[status] [varchar](255) NULL,
	[profile_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[conversations](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[created_at] [datetime2](6) NOT NULL,
	[profile1_id] [bigint] NOT NULL,
	[profile2_id] [bigint] NOT NULL,
	[last_message_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[email_verification_token](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[expiry_date] [datetime2](6) NULL,
	[token] [varchar](255) NULL,
	[user_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[genre](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[image](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[description] [varchar](255) NULL,
	[url] [varchar](255) NOT NULL,
	[filename] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[instrument](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[lesson](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[availability_id] [bigint] NOT NULL,
	[tuition_id] [bigint] NOT NULL,
	[lesson_status] [varchar](255) NULL,
	[lesson_type] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UKa999ysar25smtr23lk75elmpc] UNIQUE NONCLUSTERED 
(
	[availability_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[lesson_request](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[requested_time] [datetime2](6) NULL,
	[status] [varchar](255) NULL,
	[student_id] [bigint] NOT NULL,
	[tutor_id] [bigint] NOT NULL,
	[availability_id] [bigint] NULL,
	[lesson_type] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [unique_student_availability] UNIQUE NONCLUSTERED 
(
	[availability_id] ASC,
	[student_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[messages](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[content] [varchar](255) NOT NULL,
	[is_read] [bit] NOT NULL,
	[timestamp] [datetime2](6) NOT NULL,
	[conversation_id] [bigint] NOT NULL,
	[sender_profile_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[notifications](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[message] [varchar](255) NULL,
	[read] [bit] NULL,
	[timestamp] [datetime2](6) NULL,
	[type] [varchar](255) NULL,
	[user_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[password_reset_token](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[expiry_date] [datetime2](6) NOT NULL,
	[token] [varchar](255) NOT NULL,
	[user_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UKg0guo4k8krgpwuagos61oc06j] UNIQUE NONCLUSTERED 
(
	[token] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[password_reset_tokens](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[user_id] [bigint] NOT NULL,
	[token] [varchar](255) NOT NULL,
	[expiry_date] [datetime] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[payment](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[amount] [float] NULL,
	[due_date] [datetime2](6) NULL,
	[invoice_url] [varchar](255) NULL,
	[status] [varchar](255) NULL,
	[tuition_id] [bigint] NULL,
	[lesson_id] [bigint] NULL,
	[paid_on] [datetime2](6) NULL,
	[reminder_sent_on] [datetime2](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[price](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[description] [varchar](255) NULL,
	[period] [varchar](255) NULL,
	[rate] [float] NULL,
	[standard_pricing] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[profile](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[bio] [varchar](255) NULL,
	[display_name] [varchar](255) NULL,
	[profile_type] [varchar](255) NULL,
	[app_user_id] [bigint] NOT NULL,
	[tuition_region_id] [bigint] NULL,
	[image_id] [bigint] NULL,
	[average_rating] [float] NOT NULL,
	[lesson_type] [varchar](255) NULL,
	[deleted_at] [datetime2](6) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[profile_genre](
	[profile_id] [bigint] NOT NULL,
	[genre_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[profile_id] ASC,
	[genre_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[profile_instrument](
	[profile_id] [bigint] NOT NULL,
	[instrument_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[profile_id] ASC,
	[instrument_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[profile_instrument_qualification](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[instrument_id] [bigint] NULL,
	[profile_id] [bigint] NULL,
	[qualification_id] [bigint] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[profile_price](
	[profile_id] [bigint] NOT NULL,
	[price_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[profile_id] ASC,
	[price_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[qualification](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[region](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NULL,
	[parent_region_id] [bigint] NULL,
	[latitude] [float] NULL,
	[longitude] [float] NULL,
	[country] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[review](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[comment] [varchar](255) NULL,
	[profile_id] [bigint] NOT NULL,
	[rating] [bigint] NOT NULL,
	[reviewer_name] [varchar](255) NULL,
	[reviewer_profile_id] [bigint] NOT NULL,
	[title] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[role](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tuition](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[active_tuition] [bit] NOT NULL,
	[end_date] [date] NULL,
	[start_date] [date] NOT NULL,
	[student_id] [bigint] NOT NULL,
	[tutor_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user_roles](
	[user_id] [bigint] NOT NULL,
	[role_id] [bigint] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC,
	[role_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [idx_availability_start_end] ON [dbo].[availability]
(
	[start_time] ASC,
	[end_time] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE UNIQUE NONCLUSTERED INDEX [UKbap2ig9x1dq1y1revhfjkk64l] ON [dbo].[conversations]
(
	[last_message_id] ASC
)
WHERE ([last_message_id] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE UNIQUE NONCLUSTERED INDEX [UK1sxbwflvq4skafkocq315i9dt] ON [dbo].[email_verification_token]
(
	[user_id] ASC
)
WHERE ([user_id] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [idx_student] ON [dbo].[lesson_request]
(
	[student_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [idx_student_tutor] ON [dbo].[lesson_request]
(
	[student_id] ASC,
	[tutor_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [idx_tutor] ON [dbo].[lesson_request]
(
	[tutor_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
CREATE NONCLUSTERED INDEX [idx_price_period_rate] ON [dbo].[price]
(
	[period] ASC,
	[rate] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
CREATE UNIQUE NONCLUSTERED INDEX [UKcjdftjkdsmphwm95b66axahvn] ON [dbo].[profile]
(
	[image_id] ASC
)
WHERE ([image_id] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[price] ADD  DEFAULT ((1)) FOR [standard_pricing]
GO
ALTER TABLE [dbo].[profile] ADD  DEFAULT ((0)) FOR [average_rating]
GO
ALTER TABLE [dbo].[profile] ADD  DEFAULT ('ONLINE') FOR [lesson_type]
GO
ALTER TABLE [dbo].[app_user]  WITH CHECK ADD  CONSTRAINT [FKl68nwoh0q1mcmgf9qx9h12miw] FOREIGN KEY([address_id])
REFERENCES [dbo].[address] ([id])
GO
ALTER TABLE [dbo].[app_user] CHECK CONSTRAINT [FKl68nwoh0q1mcmgf9qx9h12miw]
GO
ALTER TABLE [dbo].[availability]  WITH CHECK ADD  CONSTRAINT [FK6q0oxv9la5phtufeb1o484lgd] FOREIGN KEY([profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[availability] CHECK CONSTRAINT [FK6q0oxv9la5phtufeb1o484lgd]
GO
ALTER TABLE [dbo].[conversations]  WITH CHECK ADD  CONSTRAINT [FKd9sy3cjuppb511olt5pv3ixe] FOREIGN KEY([last_message_id])
REFERENCES [dbo].[messages] ([id])
GO
ALTER TABLE [dbo].[conversations] CHECK CONSTRAINT [FKd9sy3cjuppb511olt5pv3ixe]
GO
ALTER TABLE [dbo].[conversations]  WITH CHECK ADD  CONSTRAINT [FKeyoxqd5rsy295eosv690fknrt] FOREIGN KEY([profile1_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[conversations] CHECK CONSTRAINT [FKeyoxqd5rsy295eosv690fknrt]
GO
ALTER TABLE [dbo].[conversations]  WITH CHECK ADD  CONSTRAINT [FKgb2ucex53v8adl04ypo0tgb66] FOREIGN KEY([profile2_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[conversations] CHECK CONSTRAINT [FKgb2ucex53v8adl04ypo0tgb66]
GO
ALTER TABLE [dbo].[email_verification_token]  WITH CHECK ADD  CONSTRAINT [FKnq8mjpxqc5bb56pk23ddkkjy0] FOREIGN KEY([user_id])
REFERENCES [dbo].[app_user] ([id])
GO
ALTER TABLE [dbo].[email_verification_token] CHECK CONSTRAINT [FKnq8mjpxqc5bb56pk23ddkkjy0]
GO
ALTER TABLE [dbo].[lesson]  WITH CHECK ADD  CONSTRAINT [FKe87cdxin1m5kuvt35p5mvqq7c] FOREIGN KEY([availability_id])
REFERENCES [dbo].[availability] ([id])
GO
ALTER TABLE [dbo].[lesson] CHECK CONSTRAINT [FKe87cdxin1m5kuvt35p5mvqq7c]
GO
ALTER TABLE [dbo].[lesson]  WITH CHECK ADD  CONSTRAINT [FKmxs4eigpqbts1314edobujx1d] FOREIGN KEY([tuition_id])
REFERENCES [dbo].[tuition] ([id])
GO
ALTER TABLE [dbo].[lesson] CHECK CONSTRAINT [FKmxs4eigpqbts1314edobujx1d]
GO
ALTER TABLE [dbo].[lesson_request]  WITH CHECK ADD  CONSTRAINT [FK1h3py6wrem53fplm5u03lttrd] FOREIGN KEY([student_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[lesson_request] CHECK CONSTRAINT [FK1h3py6wrem53fplm5u03lttrd]
GO
ALTER TABLE [dbo].[lesson_request]  WITH CHECK ADD  CONSTRAINT [FK9273p4jlga4yepiygykjg4vjl] FOREIGN KEY([availability_id])
REFERENCES [dbo].[availability] ([id])
GO
ALTER TABLE [dbo].[lesson_request] CHECK CONSTRAINT [FK9273p4jlga4yepiygykjg4vjl]
GO
ALTER TABLE [dbo].[lesson_request]  WITH CHECK ADD  CONSTRAINT [FKpvlxsuset88vtafdy7vj95ip3] FOREIGN KEY([tutor_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[lesson_request] CHECK CONSTRAINT [FKpvlxsuset88vtafdy7vj95ip3]
GO
ALTER TABLE [dbo].[messages]  WITH CHECK ADD  CONSTRAINT [FK3tolj1f526oo9xso1l9ioabwi] FOREIGN KEY([sender_profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[messages] CHECK CONSTRAINT [FK3tolj1f526oo9xso1l9ioabwi]
GO
ALTER TABLE [dbo].[messages]  WITH CHECK ADD  CONSTRAINT [FKt492th6wsovh1nush5yl5jj8e] FOREIGN KEY([conversation_id])
REFERENCES [dbo].[conversations] ([id])
GO
ALTER TABLE [dbo].[messages] CHECK CONSTRAINT [FKt492th6wsovh1nush5yl5jj8e]
GO
ALTER TABLE [dbo].[password_reset_token]  WITH CHECK ADD  CONSTRAINT [FKli7wollcmb8tibymo3s94o57h] FOREIGN KEY([user_id])
REFERENCES [dbo].[app_user] ([id])
GO
ALTER TABLE [dbo].[password_reset_token] CHECK CONSTRAINT [FKli7wollcmb8tibymo3s94o57h]
GO
ALTER TABLE [dbo].[password_reset_tokens]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[app_user] ([id])
GO
ALTER TABLE [dbo].[payment]  WITH CHECK ADD  CONSTRAINT [FKi2ps7vu7ikgn2xe7u9urfgruu] FOREIGN KEY([lesson_id])
REFERENCES [dbo].[lesson] ([id])
GO
ALTER TABLE [dbo].[payment] CHECK CONSTRAINT [FKi2ps7vu7ikgn2xe7u9urfgruu]
GO
ALTER TABLE [dbo].[payment]  WITH CHECK ADD  CONSTRAINT [FKm3d37ueh7laaofh1fkr1t4wp3] FOREIGN KEY([tuition_id])
REFERENCES [dbo].[tuition] ([id])
GO
ALTER TABLE [dbo].[payment] CHECK CONSTRAINT [FKm3d37ueh7laaofh1fkr1t4wp3]
GO
ALTER TABLE [dbo].[profile]  WITH CHECK ADD  CONSTRAINT [FK4mho4jnrk0lvbtnkqyqti93r5] FOREIGN KEY([tuition_region_id])
REFERENCES [dbo].[region] ([id])
GO
ALTER TABLE [dbo].[profile] CHECK CONSTRAINT [FK4mho4jnrk0lvbtnkqyqti93r5]
GO
ALTER TABLE [dbo].[profile]  WITH CHECK ADD  CONSTRAINT [FKjvw51ll74pvplckkn054pakw9] FOREIGN KEY([image_id])
REFERENCES [dbo].[image] ([id])
GO
ALTER TABLE [dbo].[profile] CHECK CONSTRAINT [FKjvw51ll74pvplckkn054pakw9]
GO
ALTER TABLE [dbo].[profile]  WITH CHECK ADD  CONSTRAINT [FKqi37q2rokrlgupng4qq5ikqf0] FOREIGN KEY([app_user_id])
REFERENCES [dbo].[app_user] ([id])
GO
ALTER TABLE [dbo].[profile] CHECK CONSTRAINT [FKqi37q2rokrlgupng4qq5ikqf0]
GO
ALTER TABLE [dbo].[profile_genre]  WITH CHECK ADD  CONSTRAINT [FK63w8wvoi04fjncaos8b7gkrx9] FOREIGN KEY([profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[profile_genre] CHECK CONSTRAINT [FK63w8wvoi04fjncaos8b7gkrx9]
GO
ALTER TABLE [dbo].[profile_genre]  WITH CHECK ADD  CONSTRAINT [FKbcum71ct55r82y62nvftibifa] FOREIGN KEY([genre_id])
REFERENCES [dbo].[genre] ([id])
GO
ALTER TABLE [dbo].[profile_genre] CHECK CONSTRAINT [FKbcum71ct55r82y62nvftibifa]
GO
ALTER TABLE [dbo].[profile_instrument]  WITH CHECK ADD  CONSTRAINT [FKb3pfdjtmw0j1tvlxg0ythejen] FOREIGN KEY([instrument_id])
REFERENCES [dbo].[instrument] ([id])
GO
ALTER TABLE [dbo].[profile_instrument] CHECK CONSTRAINT [FKb3pfdjtmw0j1tvlxg0ythejen]
GO
ALTER TABLE [dbo].[profile_instrument]  WITH CHECK ADD  CONSTRAINT [FKs90av2if78lv97fljee3tcvbm] FOREIGN KEY([profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[profile_instrument] CHECK CONSTRAINT [FKs90av2if78lv97fljee3tcvbm]
GO
ALTER TABLE [dbo].[profile_instrument_qualification]  WITH CHECK ADD  CONSTRAINT [FK289nj1e1j95hqtjc79920wgpa] FOREIGN KEY([qualification_id])
REFERENCES [dbo].[qualification] ([id])
GO
ALTER TABLE [dbo].[profile_instrument_qualification] CHECK CONSTRAINT [FK289nj1e1j95hqtjc79920wgpa]
GO
ALTER TABLE [dbo].[profile_instrument_qualification]  WITH CHECK ADD  CONSTRAINT [FKa1wh5qgsnuyhdqyos5bt92l0r] FOREIGN KEY([profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[profile_instrument_qualification] CHECK CONSTRAINT [FKa1wh5qgsnuyhdqyos5bt92l0r]
GO
ALTER TABLE [dbo].[profile_instrument_qualification]  WITH CHECK ADD  CONSTRAINT [FKuxgf94fwhpud6yrreum5pp9h] FOREIGN KEY([instrument_id])
REFERENCES [dbo].[instrument] ([id])
GO
ALTER TABLE [dbo].[profile_instrument_qualification] CHECK CONSTRAINT [FKuxgf94fwhpud6yrreum5pp9h]
GO
ALTER TABLE [dbo].[profile_price]  WITH CHECK ADD  CONSTRAINT [FKae4ve5rwobb297e2l4e2d7r15] FOREIGN KEY([price_id])
REFERENCES [dbo].[price] ([id])
GO
ALTER TABLE [dbo].[profile_price] CHECK CONSTRAINT [FKae4ve5rwobb297e2l4e2d7r15]
GO
ALTER TABLE [dbo].[profile_price]  WITH CHECK ADD  CONSTRAINT [FKg54ptxir3far74rm9jtgao4ej] FOREIGN KEY([profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[profile_price] CHECK CONSTRAINT [FKg54ptxir3far74rm9jtgao4ej]
GO
ALTER TABLE [dbo].[region]  WITH CHECK ADD  CONSTRAINT [FKeojo2oralh7nh9d3jkyxseuyj] FOREIGN KEY([parent_region_id])
REFERENCES [dbo].[region] ([id])
GO
ALTER TABLE [dbo].[region] CHECK CONSTRAINT [FKeojo2oralh7nh9d3jkyxseuyj]
GO
ALTER TABLE [dbo].[review]  WITH CHECK ADD  CONSTRAINT [FK8al2deis8pp34djo6gw6arss0] FOREIGN KEY([profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[review] CHECK CONSTRAINT [FK8al2deis8pp34djo6gw6arss0]
GO
ALTER TABLE [dbo].[review]  WITH CHECK ADD  CONSTRAINT [FKhsl6jfepane4mr1375bp1diq4] FOREIGN KEY([reviewer_profile_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[review] CHECK CONSTRAINT [FKhsl6jfepane4mr1375bp1diq4]
GO
ALTER TABLE [dbo].[tuition]  WITH CHECK ADD  CONSTRAINT [FKg3iewgkhrli0bwy2vyx1h7hib] FOREIGN KEY([tutor_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[tuition] CHECK CONSTRAINT [FKg3iewgkhrli0bwy2vyx1h7hib]
GO
ALTER TABLE [dbo].[tuition]  WITH CHECK ADD  CONSTRAINT [FKp76ejynsd49h84i1y7ml4xw0] FOREIGN KEY([student_id])
REFERENCES [dbo].[profile] ([id])
GO
ALTER TABLE [dbo].[tuition] CHECK CONSTRAINT [FKp76ejynsd49h84i1y7ml4xw0]
GO
ALTER TABLE [dbo].[user_roles]  WITH CHECK ADD FOREIGN KEY([role_id])
REFERENCES [dbo].[role] ([id])
GO
ALTER TABLE [dbo].[user_roles]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[app_user] ([id])
GO
ALTER TABLE [dbo].[lesson]  WITH CHECK ADD CHECK  (([lesson_status]='CANCELED' OR [lesson_status]='COMPLETED' OR [lesson_status]='CONFIRMED'))
GO
ALTER TABLE [dbo].[lesson]  WITH CHECK ADD CHECK  (([lesson_type]='ONLINE_AND_INPERSON' OR [lesson_type]='INPERSON' OR [lesson_type]='ONLINE'))
GO
ALTER TABLE [dbo].[lesson_request]  WITH CHECK ADD CHECK  (([lesson_type]='ONLINE_AND_INPERSON' OR [lesson_type]='INPERSON' OR [lesson_type]='ONLINE'))
GO
ALTER TABLE [dbo].[lesson_request]  WITH CHECK ADD CHECK  (([status]='DECLINED' OR [status]='CONFIRMED' OR [status]='PENDING'))
GO
ALTER TABLE [dbo].[price]  WITH CHECK ADD CHECK  (([period]='CUSTOM' OR [period]='HALF_HOUR' OR [period]='TWO_HOURS' OR [period]='ONE_HOUR'))
GO
ALTER TABLE [dbo].[profile]  WITH CHECK ADD CHECK  (([profile_type]='PARENT' OR [profile_type]='TUTOR' OR [profile_type]='STUDENT'))
GO


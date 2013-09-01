drop table T_DINING if exists;

create table T_DINING (
	ID char(36) primary key, 
	CREDIT_CARD_NUMBER varchar(16) not null,
	MERCHANT_NUMBER varchar(10) not null,
	AMOUNT double not null, 
	DINING_DATE timestamp not null, 
	CONFIRMED smallint not null
);

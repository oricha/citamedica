import { PrismaClient } from '@prisma/client';
import { NextResponse } from 'next/server';
import { getServerSession } from 'next-auth/next';
import { authOptions } from '../../auth/[...nextauth]/route';

const prisma = new PrismaClient();

export async function GET() {
  try {
    const session = await getServerSession(authOptions);
    if (!session || session.user.role !== 'SYSTEM_ADMIN') {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }
    const clinics = await prisma.clinic.findMany({ include: { doctors: true } });
    return NextResponse.json({ data: clinics });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching clinics' }, { status: 500 });
  }
}

